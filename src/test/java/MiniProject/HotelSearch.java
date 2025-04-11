package MiniProject;
//import java.util.concurrent.CompletableFuture;
import java.io.File; 
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.List;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

 
public class HotelSearch {
    public static WebDriver driver;
    String baseUrl = "https://www.trivago.in/";
    static TakesScreenshot sc;
    String excelFilePath = System.getProperty("user.dir") + "\\ExcelData\\HotelData.xlsx";
    
    public WebDriver createDriver() {
         driver = new ChromeDriver();
         driver.manage().deleteAllCookies();
    	 driver.get(baseUrl);
    	 return driver;
    }
    public void searchCity() throws InterruptedException  {
        WebElement searchField=driver.findElement(By.xpath("//*[@id=\"input-auto-complete\"]"));
        searchField.sendKeys("Mumbai");
        Thread.sleep(2000);
        searchField.click();
        takeScreenshot("SearchCity");
        
    }
 
    public void checkInOut() throws InterruptedException {
       String checkin_month = "July";
       String checkin_year = "2025";
    	driver.findElement(By.xpath("//*[@id=\"__next\"]/div[1]/div[2]/section[1]/div[2]/div/div/fieldset/button[1]")).click();        
    	Thread.sleep(2000);
        while (true) {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
            WebElement currentDisplayElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id='__next']/div[1]/div[2]/section[1]/div[2]/div/div[2]/div/div/div/div[2]/div/div/div[1]/h3")));
            
            String currentDisplay = currentDisplayElement.getText();
            String[] current = currentDisplay.split(" ");
            String currentMonth = current[0];
            String currentYear = current[1];
            if (currentMonth.equals(checkin_month) && currentYear.equals(checkin_year)) {
                break;
            }
            driver.findElement(By.xpath("//*[@id=\"__next\"]/div[1]/div[2]/section[1]/div[2]/div/div[2]/div/div/div/div[2]/div/button[2]")).click();
            Thread.sleep(2000);
        }
    	driver.findElement(By.xpath("//button[@data-testid=\"valid-calendar-day-2025-07-27\"]")).click();
        Thread.sleep(2000);
        driver.findElement(By.xpath("//button[@data-testid=\"valid-calendar-day-2025-07-28\"]")).click();
        Thread.sleep(2000);
        takeScreenshot("CheckInOut");
    }

	public void guestsAndrooms() throws InterruptedException {
   	 	driver.findElement(By.cssSelector("button[data-testid='search-form-guest-selector']")).click();
   	 	@SuppressWarnings("deprecation")
		int a = Integer.parseInt(driver.findElement(By.xpath("//input[@class=\"h1ZWRl\"][1]")).getAttribute("value")); 
   	 	while (a > 1) {
   	 		driver.findElement(By.xpath("//button[@data-testid='adults-amount-minus-button']")).click();
   	 		a--;
   	 	}	
		driver.findElement(By.xpath("//button[normalize-space()='Apply']")).click();
		takeScreenshot("GuestsAndRooms");
    }
    
    public void search() throws InterruptedException {
    	driver.findElement(By.xpath("//button[@class='_3tjlp_']")).click();
    	Thread.sleep(2000);
    	takeScreenshot("Search");
    }
    
	public void sortBy() throws InterruptedException {
		WebElement sort=driver.findElement(By.xpath("/html/body/div[1]/div[1]/main/div[2]/div/div/div/div/div[1]/div/button/span/span[2]"));
	    sort.click();
		Thread.sleep(2000);
		WebElement rating=driver.findElement(By.xpath("//input[@value='3']"));
		rating.click();
		Thread.sleep(2000);
		driver.findElement(By.xpath("//button[normalize-space()='Apply']")).click();
		Thread.sleep(2000);
		takeScreenshot("SortBy");
	}
	public void verifyHotelDetails() throws InterruptedException, IOException {
	    Thread.sleep(5000); // Ensure search results load

	    List<WebElement> hotelNames = driver.findElements(By.cssSelector("span[itemprop='name']")); // Hotel name
	    List<WebElement> hotelPrices = driver.findElements(By.cssSelector("div[data-testid='recommended-price']")); // Hotel price
	    List<WebElement> hotelRatings = driver.findElements(By.cssSelector("span[itemprop='ratingValue']")); // Hotel rating

	    if (hotelNames.isEmpty() || hotelPrices.isEmpty() || hotelRatings.isEmpty()) {
	        System.out.println("No hotels, prices, or ratings found. Please check the selectors or wait time.");
	        return;
	    }

	    Workbook workbook = new XSSFWorkbook();
	    Sheet sheet = workbook.createSheet("Hotels");

	    Row header = sheet.createRow(0);
	    header.createCell(0).setCellValue("Hotel Name");
	    header.createCell(1).setCellValue("Price");
	    header.createCell(2).setCellValue("Rating");

	    int count = 0;
	    for (int i = 0; i < hotelNames.size() && count < 5; i++) {
	        String hotelName = hotelNames.get(i).getText();
	        String hotelPrice = hotelPrices.get(i).getText();
	        String hotelRating = hotelRatings.get(i).getText();

	        System.out.println("Hotel " + (count + 1) + ": " + hotelName + " - " + hotelPrice + " - " + hotelRating);
	        
	        Row row = sheet.createRow(count + 1);
	        row.createCell(0).setCellValue(hotelName);
	        row.createCell(1).setCellValue(hotelPrice);
	        row.createCell(2).setCellValue(hotelRating);
	        count++;
	    }

	    FileOutputStream fileOut = new FileOutputStream(excelFilePath);
	    workbook.write(fileOut);
	    fileOut.close();
	    workbook.close();

	    System.out.println("Hotel data written to Excel: " + excelFilePath);
	   // takeScreenshot("HotelVerification");
	}
    public void readExcelData() throws IOException {
        FileInputStream file = new FileInputStream(excelFilePath);
        Workbook workbook = new XSSFWorkbook(file);
        Sheet sheet = workbook.getSheet("Hotels");

        System.out.println("\nReading from Excel File:");
        for (Row row : sheet) {
            for (Cell cell : row) {
                System.out.print(cell.getStringCellValue() + "\t");
            }
            System.out.println();
        }

        workbook.close();
        file.close();
    }

    public static void takeScreenshot(String fileName) {
        sc = (TakesScreenshot) driver;
        File screenshot = sc.getScreenshotAs(OutputType.FILE);
        File destination = new File(System.getProperty("user.dir") + "\\Screenshots\\" + fileName + ".png");
        screenshot.renameTo(destination);
        
    }

    public static void main(String[] args) throws Exception{
        HotelSearch h = new HotelSearch();
        h.createDriver();
        driver.manage().window().maximize();
        h.guestsAndrooms();
        h.searchCity();
        h.checkInOut();
        h.search();
        h.sortBy();
        h.verifyHotelDetails(); // Verify and write to Excel
        h.readExcelData(); // Read data from Excel and print

    }
}
 