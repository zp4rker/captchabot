package com.zp4rker.captchabot

import com.zp4rker.captchabot.captcha.Captcha
import com.zp4rker.captchabot.captcha.CaptchaGenerator
import com.zp4rker.captchabot.cmd.VerifyCommand
import com.zp4rker.captchabot.lstnr.MessageListener
import com.zp4rker.core.discord.command.Command
import com.zp4rker.core.discord.command.CommandHandler
import com.zp4rker.core.discord.config.Config
import net.dv8tion.jda.api.AccountType
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.MessageType
import net.dv8tion.jda.api.entities.TextChannel
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.api.hooks.AnnotatedEventManager
import net.dv8tion.jda.api.hooks.SubscribeEvent
import java.awt.Color
import java.util.concurrent.TimeUnit

val config = Config.load("config.json")
val captchas = mutableListOf<Captcha>()

fun main() {
    val handler = CommandHandler(CaptchaBot.prefix)
    handler.registerCommands(VerifyCommand)

    JDABuilder(AccountType.BOT).setToken(CaptchaBot.token).run {
        setEventManager(AnnotatedEventManager())
        addEventListeners(handler, MessageListener)
    }.build()
}

object CaptchaBot {
    val token = config.optString("token") ?: ""
    val prefix = config.getString("prefix") ?: "."
    val role = config.optLong("role", 0)
}