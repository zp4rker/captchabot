package com.zp4rker.captchabot

import com.zp4rker.captchabot.lstnr.JoinListener
import com.zp4rker.disbot.Bot

var role: Long = 0
var channel: Long = 0

fun main(args: Array<String>) {
    val token = args[0]
    val prefix = args[1]
    role = args[2].toLong()
    channel = args[3].toLong()

    val bot = Bot.create {
        name = "CaptchaBot"
        version = "1.0.0-release"

        this.token = token
        this.prefix = prefix
    }

    bot.addEventListener(JoinListener())

    /*API.on<GuildMessageReceivedEvent>({ !it.author.isBot && it.channel.idLong == channel }) {
        it.message.delete().submitAfter(1, TimeUnit.SECONDS).exceptionally { null }
    }*/
}