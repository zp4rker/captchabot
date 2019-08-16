package com.zp4rker.captchabot.lstnr

import com.zp4rker.captchabot.CaptchaBot
import com.zp4rker.captchabot.captcha.CaptchaGenerator
import com.zp4rker.captchabot.captchas
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.api.hooks.SubscribeEvent
import java.util.concurrent.TimeUnit

object MessageListener {
    @SubscribeEvent
    fun onMessage(event: GuildMessageReceivedEvent) {
        val member = event.member ?: return
        val role = event.guild.getRoleById(CaptchaBot.role) ?: return
        if (member.roles.contains(role)) return
        if (captchas.none { it.user == member.user.idLong }) return

        event.message.delete().submit().exceptionally { null }
        val captcha = captchas.find { it.user == member.user.idLong } ?: return
        val guess = event.message.contentRaw.replace(" ", "")

        if (!captcha.code.equals(guess, true)) {
            captcha.attempts++
            if (captcha.attempts >= 3) {
                event.channel.sendMessage("${event.author.asMention}, you failed 3 attempts! Regenerating captcha...").queue { it.delete().queueAfter(3, TimeUnit.SECONDS) }
                event.channel.getHistoryAround(captcha.message, 1).queue { it.retrievedHistory[0].delete().queue() }
                captchas.remove(captcha)

                val newCaptcha = CaptchaGenerator.new()
                newCaptcha.user = event.author.idLong
                captchas.add(newCaptcha)
                event.channel.sendFile(newCaptcha.image, "captcha.png").append("${event.author.asMention}, type out these characters to verify!").queueAfter(3, TimeUnit.SECONDS) { newCaptcha.message = it.idLong }
            }
            return
        }

        event.channel.getHistoryAround(captcha.message, 1).queue { it.retrievedHistory[0].delete().queue() }
        event.channel.sendMessage("${event.author.asMention}, you have successfully verified. Thank you!").queue {
            event.guild.addRoleToMember(member, role).queueAfter(3, TimeUnit.SECONDS) { _ -> it.delete().queue() }
        }
        captchas.remove(captcha)
    }
}