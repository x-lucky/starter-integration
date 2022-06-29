package cn.xlucky.framework.common.util;

import com.aspose.pdf.Document;
import com.aspose.pdf.SaveFormat;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author: 聂祥
 * @date: 2022/6/28 17:23
 * @describe:
 * @vision: 1.0
 */
public class PdfUtil {

	public static void main(String[] args) throws IOException {
//		modifyPDFJar();
		getpdfLicense();
		ConvertPDFtoWord();
	}

	public static void ConvertPDFtoWord() {
		// Open the source PDF document
		Document pdfDocument = new Document("C:\\Users\\10065\\Desktop\\test.pdf");
		// Save the file into MS document format
		pdfDocument.save("C:\\Users\\10065\\Desktop\\testDoc.doc", SaveFormat.Doc);
	}

	public static boolean getpdfLicense() {
		boolean result2 = false;
		try {
			String license2 = "<License>\n"
				+ "  <Data>\n"
				+ "    <Products>\n"
				+ "      <Product>Aspose.Total for Java</Product>\n"
				+ "      <Product>Aspose.Words for Java</Product>\n"
				+ "    </Products>\n"
				+ "    <EditionType>Enterprise</EditionType>\n"
				+ "    <SubscriptionExpiry>20991231</SubscriptionExpiry>\n"
				+ "    <LicenseExpiry>20991231</LicenseExpiry>\n"
				+ "    <SerialNumber>8bfe198c-7f0c-4ef8-8ff0-acc3237bf0d7</SerialNumber>\n"
				+ "  </Data>\n"
				+ "  <Signature>111</Signature>\n"
				+ "</License>";
			InputStream is2 = new ByteArrayInputStream(
				license2.getBytes("UTF-8"));
			com.aspose.pdf.License asposeLic2 = new com.aspose.pdf.License();
			asposeLic2.setLicense(is2);
			result2 = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result2;
	}


	/**
	 * 修改pdf jar包里面的校验
	 */
	public static void modifyPDFJar(){
		try {
			//这一步是完整的jar包路径,选择自己解压的jar目录
			ClassPool.getDefault().insertClassPath("C:\\Users\\10065\\Desktop\\aspose-pdf-21.11.jar");
			//获取指定的class文件对象
			CtClass zzZJJClass = ClassPool.getDefault().getCtClass("com.aspose.pdf.l9f");
			//从class对象中解析获取所有方法
			CtMethod[] methodA = zzZJJClass.getDeclaredMethods();
			for (CtMethod ctMethod : methodA) {
				//获取方法获取参数类型
				CtClass[] ps = ctMethod.getParameterTypes();
				//筛选同名方法，入参是Document
				if (ps.length == 1 && ctMethod.getName().equals("lI") && ps[0].getName().equals("java.io.InputStream")) {
					System.out.println("ps[0].getName==" + ps[0].getName());
					//替换指定方法的方法体
					ctMethod.setBody("{this.l0if = com.aspose.pdf.l10if.lf;com.aspose.pdf.internal.imaging.internal.p71.Helper.help1();lI(this);}");
				}
			}
			//这一步就是将破译完的代码放在桌面上
			zzZJJClass.writeFile("C:\\Users\\10065\\Desktop");

		} catch (Exception e) {
			System.out.println("错误==" + e);
		}
	}
}
