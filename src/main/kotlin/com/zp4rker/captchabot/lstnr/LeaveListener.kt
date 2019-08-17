package com.zp4rker.captchabot.lstnr

import com.zp4rker.captchabot.CaptchaBot
import com.zp4rker.captchabot.captchas
import net.dv8tion.jda.api.events.guild.member.GuildMemberLeaveEvent
import net.dv8tion.jda.api.hooks.SubscribeEvent

object LeaveListener {
    @SubscribeEvent
    fun onLeave(event: GuildMemberLeaveEvent) {
        if (captchas.none { it.user == event.user.idLong }) return

        val captcha = captchas.find { it.user == event.user.idLong } ?: return
        val channel = event.jda.getTextChannelById(CaptchaBot.channel) ?: return
        channel.getHistoryAround(captcha.message, 1).queue { it.retrievedHistory[0].delete().queue() }
        captchas.remove(captcha)
    }
}