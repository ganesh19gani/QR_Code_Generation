package com.ojas.qrcode.controller;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeWriter;
import com.ojas.qrcode.model.PaytmRequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.nio.file.FileSystems;
import java.nio.file.Path;

@RestController
public class PaytmQRCodeController {
    public static String QRCODE_PATH = "";

    @PostMapping("/generateQRCode")
    public void writeQRCode(@RequestBody PaytmRequestBody paytmRequestBody, HttpServletResponse httpResponse) throws Exception {
        String qrcode = QRCODE_PATH + "QRCODE.png";
        QRCodeWriter writer = new QRCodeWriter();
        BitMatrix bitMatrix = writer.encode(
                paytmRequestBody.getUserName() + "\n" + paytmRequestBody.getAccountNo() + "\n"
                        + paytmRequestBody.getAccountType() + "\n" + paytmRequestBody.getMobileNo(),
                BarcodeFormat.QR_CODE, 350, 350);
        Path path = FileSystems.getDefault().getPath(qrcode);
        MatrixToImageWriter.writeToPath(bitMatrix, "PNG", path);
        //return "QRCODE is generated successfully....";

        // reading the QRCode from the file and returning
        String outputPDFFileName = QRCODE_PATH + "QRCODE.png";
        httpResponse.setContentType("application/png");
        httpResponse.setHeader("Content-Disposition", "inline; filename=" + "qrCode" + ".png");

        OutputStream out = httpResponse.getOutputStream();
        FileInputStream fi = new FileInputStream(outputPDFFileName);
        byte[] buffer = new byte[5000];
        int n;
        while ((n = fi.read(buffer)) > 0) {
            out.write(buffer, 0, n);
        }
        fi.close();

        out.close();

    }


    @GetMapping("/readQRCode")
    public String readQRCode() throws Exception {
        String qrcodeImagePath = QRCODE_PATH + "QRCODE.png";
        BufferedImage bufferedImage = ImageIO.read(new File(qrcodeImagePath));
        LuminanceSource luminanceSource = new BufferedImageLuminanceSource(bufferedImage);
        BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(luminanceSource));
        Result result = new MultiFormatReader().decode(binaryBitmap);
        return result.getText();

    }

}

