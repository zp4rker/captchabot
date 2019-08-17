package com.zp4rker.captchabot

import com.zp4rker.captchabot.captcha.Captcha
import com.zp4rker.captchabot.cmd.VerifyCommand
import com.zp4rker.captchabot.lstnr.GuessListener
import com.zp4rker.captchabot.lstnr.LeaveListener
import com.zp4rker.captchabot.lstnr.MessageListener
import com.zp4rker.core.discord.command.CommandHandler
import com.zp4rker.core.discord.config.Config
import net.dv8tion.jda.api.AccountType
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.hooks.AnnotatedEventManager

val config = Config.load("config.json")
val captchas = mutableListOf<Captcha>()

fun main() {
    val handler = CommandHandler(CaptchaBot.prefix)
    handler.registerCommands(VerifyCommand)

    JDABuilder(AccountType.BOT).setToken(CaptchaBot.token).run {
        setEventManager(AnnotatedEventManager())
        addEventListeners(handler, GuessListener, MessageListener, LeaveListener)
    }.build()
}

object CaptchaBot {
    val token = config.optString("token") ?: ""
    val prefix = config.getString("prefix") ?: "."
    val role = config.getLong("role")
    val channel = config.getLong("channel")
}