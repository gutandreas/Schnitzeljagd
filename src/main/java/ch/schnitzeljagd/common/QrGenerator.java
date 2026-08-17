package ch.schnitzeljagd.common;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

/**
 * Erzeugt die QR-Bilder für die Posten.
 * <p>
 * Fehlerkorrektur steht auf hoch: Die Zettel hängen im Schulhaus und werden
 * angefasst, geknickt und bei schlechtem Licht gescannt.
 */
@Component
public class QrGenerator {

    private static final int DEFAULT_SIZE = 400;

    public byte[] toPngBytes(String content) {
        return toPngBytes(content, DEFAULT_SIZE);
    }

    public byte[] toPngBytes(String content, int size) {
        try {
            Map<EncodeHintType, Object> hints = Map.of(
                    EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H,
                    EncodeHintType.MARGIN, 2,
                    EncodeHintType.CHARACTER_SET, "UTF-8");

            BitMatrix matrix = new QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, size, size, hints);
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(matrix, "PNG", output);
            return output.toByteArray();
        } catch (WriterException | IOException e) {
            throw new IllegalStateException("QR-Code liess sich nicht erzeugen: " + content, e);
        }
    }
}
