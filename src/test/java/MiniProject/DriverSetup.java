package MiniProject;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
 
public class DriverSetup{
	private static WebDriver driver;
	public static WebDriver getDriver() {
		driver=new ChromeDriver();
		return driver;	
	}
	
}