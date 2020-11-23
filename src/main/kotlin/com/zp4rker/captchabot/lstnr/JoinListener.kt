package com.zp4rker.captchabot.lstnr

import com.zp4rker.captchabot.captcha.CaptchaGenerator
import com.zp4rker.captchabot.channel
import com.zp4rker.captchabot.role
import com.zp4rker.disbot.extenstions.event.expect
import com.zp4rker.disbot.extenstions.event.on
import com.zp4rker.disbot.extenstions.event.unregister
import net.dv8tion.jda.api.events.GenericEvent
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.api.hooks.EventListener
import java.util.concurrent.TimeUnit

/**
 * @author zp4rker
 */
class JoinListener: EventListener {

    override fun onEvent(event: GenericEvent) {
        if (event !is GuildMemberJoinEvent) return

        val channel = event.guild.getTextChannelById(channel) ?: return
        val role = event.guild.getRoleById(role) ?: return
        var captcha = CaptchaGenerator.new()
        var message = channel.sendMessage("${event.member.asMention}, please answer this captcha to gain access to the server:")
            .addFile(captcha.image, "captcha.png")
            .complete()

        val predicate: (GuildMessageReceivedEvent) -> Boolean = { e ->
            e.member ==  event.member && !e.member!!.roles.contains(role)

        }

        channel.on(predicate) {
            it.message.delete().queue()
            val guess = it.message.contentRaw.replace(" ", "")

            if (!captcha.code.equals(guess, true)) {
                captcha.attempts++
                if (captcha.attempts >= 3) {
                    message.delete().queue()
                    it.channel.sendMessage("${it.author.asMention}, you have failed 3 attempts! Regenerating captcha...").queue { msg -> msg.delete().queueAfter(3, TimeUnit.SECONDS) }

                    captcha = CaptchaGenerator.new()
                    message = channel.sendMessage("${it.member!!.asMention}, please answer this captcha to gain access to the server:")
                        .addFile(captcha.image, "captcha.png")
                        .complete()
                }
            } else {
                message.delete().queue()
                it.channel.sendMessage("${it.author.asMention}, you have successfully verified. Thank you!").queue { _ ->
                    it.guild.addRoleToMember(it.member!!, role).queueAfter(1, TimeUnit.SECONDS)
                }
                unregister()
            }
        }

        event.member.expect<GuildMemberRemoveEvent> {
            message.delete().queue()
        }
    }
}