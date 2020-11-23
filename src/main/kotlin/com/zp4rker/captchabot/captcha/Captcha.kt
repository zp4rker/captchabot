package com.zp4rker.captchabot.captcha

import org.apache.batik.anim.dom.SAXSVGDocumentFactory
import org.apache.batik.transcoder.TranscoderInput
import org.apache.batik.transcoder.TranscoderOutput
import org.apache.batik.transcoder.image.PNGTranscoder
import org.apache.batik.util.XMLResourceDescriptor
import org.w3c.dom.svg.SVGDocument
import java.awt.Color
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.*
import javax.imageio.ImageIO

object CaptchaGenerator {

    private fun randomString() = UUID.randomUUID().toString().take(4)

    private fun gradient(): Array<String> {
        val s = .8F
        val b = .9F
        val h = Random().nextFloat()

        val c1 = Color.getHSBColor(h, s, b)
        val c2 = Color.getHSBColor(h + arrayOf(.33F, .2F).random(), s, b)

        return arrayOf(String.format("#%02X%02X%02X", c1.red, c1.green, c1.blue), String.format("#%02X%02X%02X", c2.red, c2.green, c2.blue))
    }

    fun new(): Captcha {
        val text = randomString()

        val factory = SAXSVGDocumentFactory(XMLResourceDescriptor.getXMLParserClassName())
        val doc = factory.createDocument(this.javaClass.getResource("/captcha.svg").toURI().toString()) as SVGDocument
        val gradientDef = doc.firstChild.childNodes.item(7).childNodes.item(1)
        val gradient = gradient()
        gradientDef.childNodes.item(1).attributes.item(1).nodeValue = gradient[0]
        gradientDef.childNodes.item(3).attributes.item(1).nodeValue = gradient[1]
        val parent = doc.firstChild.childNodes.item(9)
        for (i in 3..9 step 2) {
            val node = parent.childNodes.item(i).childNodes.item(1)
            node.firstChild.nodeValue = text[(i - 3) / 2].toString()
        }
        val svg = TranscoderInput(doc)
        val outFile = File("${UUID.randomUUID()}.png")
        val png = TranscoderOutput(outFile.outputStream())
        PNGTranscoder().transcode(svg, png)
        png.outputStream.apply { flush();close() }
        val output = ByteArrayOutputStream()
        ImageIO.write(ImageIO.read(outFile), "png", output)
        outFile.delete()
        return Captcha(text, output.toByteArray())
    }

}

class Captcha(val code: String, val image: ByteArray, var attempts: Int = 0)

