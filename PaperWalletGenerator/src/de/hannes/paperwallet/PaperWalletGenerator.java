package de.hannes.paperwallet;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.SegwitAddress;
import org.bitcoinj.params.MainNetParams;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Image;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfString;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.draw.LineSeparator;

public class PaperWalletGenerator {

	public static void main(String[] args) {
		NetworkParameters params = MainNetParams.get();

		ECKey key = new ECKey();
		SegwitAddress segwitAddress = SegwitAddress.fromKey(params, key);
		String privateKey = key.getPrivateKeyAsWiF(params);

		Document document = new Document();
		try {
			PdfWriter instance = PdfWriter.getInstance(document, new FileOutputStream("BitcoinPaperWallet.pdf"));

			document.open();
			instance.getInfo().put(PdfName.CREATOR, new PdfString(Document.getVersion()));

			Paragraph title = new Paragraph("Bitcoin SegWit Paper Wallet");
			title.setAlignment(Element.ALIGN_CENTER);
			document.add(title);

			document.add(new Chunk(new LineSeparator()));

			Paragraph privateKeyParagraph = new Paragraph("Private Key: " + privateKey);
			privateKeyParagraph.setAlignment(Element.ALIGN_CENTER);
			privateKeyParagraph.setSpacingBefore(20);
			privateKeyParagraph.setSpacingAfter(20);
			document.add(privateKeyParagraph);

			Paragraph addressParagraph = new Paragraph("SegWit Address: " + segwitAddress.toString());
			addressParagraph.setAlignment(Element.ALIGN_CENTER);
			addressParagraph.setSpacingAfter(20);
			document.add(addressParagraph);

			Paragraph privateKeyQRParagraph = new Paragraph("Private Key QR Code");
			privateKeyQRParagraph.setAlignment(Element.ALIGN_CENTER);
			privateKeyQRParagraph.setSpacingBefore(20);
			document.add(privateKeyQRParagraph);

			Image privateKeyQR = Image.getInstance(generateQRCodeImage(privateKey));
			privateKeyQR.setAlignment(Element.ALIGN_CENTER);
			document.add(privateKeyQR);

			Paragraph addressQRParagraph = new Paragraph("SegWit Address QR Code");
			addressQRParagraph.setAlignment(Element.ALIGN_CENTER);
			addressQRParagraph.setSpacingBefore(20);
			document.add(addressQRParagraph);

			Image addressQR = Image.getInstance(generateQRCodeImage(segwitAddress.toString()));
			addressQR.setAlignment(Element.ALIGN_CENTER);
			document.add(addressQR);

		} catch (DocumentException | IOException | WriterException e) {
			System.err.println(e.getMessage());
		}

		document.close();
	}

	public static byte[] generateQRCodeImage(String text) throws WriterException, IOException {
		QRCodeWriter qrCodeWriter = new QRCodeWriter();
		BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, 200, 200);

		ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
		MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
		return pngOutputStream.toByteArray();
	}

}
