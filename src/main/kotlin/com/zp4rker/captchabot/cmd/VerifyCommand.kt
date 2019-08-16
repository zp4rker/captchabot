package com.zp4rker.captchabot.cmd

import com.zp4rker.captchabot.CaptchaBot
import com.zp4rker.captchabot.captcha.CaptchaGenerator
import com.zp4rker.captchabot.captchas
import com.zp4rker.core.discord.command.Command
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.TextChannel

object VerifyCommand : Command(aliases = arrayOf("verify"), autoDelete = true) {
    override fun handle(message: Message, channel: TextChannel, guild: Guild, args: List<String>) {
        val member = message.member ?: return
        val role = guild.getRoleById(CaptchaBot.role) ?: return
        if (captchas.any { it.user == message.author.idLong }) return
        if (member.roles.contains(role)) return

        val captcha = CaptchaGenerator.new()
        captcha.user = message.author.idLong
        channel.sendFile(captcha.image, "captcha.png").append("${message.author.asMention}, type out these characters to verify!").queue { captcha.message = it.idLong;captchas.add(captcha) }
    }
}