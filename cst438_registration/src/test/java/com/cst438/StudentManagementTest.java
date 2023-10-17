package com.cst438;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.openqa.selenium.By.id;

import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class StudentManagementTest {

    public static final String CHROME_DRIVER_FILE_LOCATION = "C:/chromedriver_win32/chromedriver.exe";
    public static final String URL = "http://localhost:3000";

    // You can use your actual student data for testing
    public static final String TEST_STUDENT_NAME = "Test Student";
    public static final String TEST_STUDENT_EMAIL = "teststudent@example.com";

    @Test
    public void addStudentTest() throws Exception {
        System.setProperty("webdriver.chrome.driver", CHROME_DRIVER_FILE_LOCATION);
        WebDriver driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

        try {
            driver.get(URL);
            Thread.sleep(1000);

            // Locate and click "Add Student" button
            driver.findElement(id("addStudent")).click();
            Thread.sleep(1000);

            // Fill in student information
            driver.findElement(id("studentName")).sendKeys(TEST_STUDENT_NAME);
            driver.findElement(id("studentEmail")).sendKeys(TEST_STUDENT_EMAIL);
            driver.findElement(id("add")).click();
            Thread.sleep(1000);

            // Verify that the new student is added
            WebElement addedStudent = driver.findElement(By.xpath("//tr[td='" + TEST_STUDENT_EMAIL + "']"));
            assertNotNull(addedStudent, "Test student not found in the student list.");

        } catch (Exception ex) {
            throw ex;
        } finally {
            driver.quit();
        }
    }

    @Test
    public void updateStudentTest() throws Exception {
        System.setProperty("webdriver.chrome.driver", CHROME_DRIVER_FILE_LOCATION);
        WebDriver driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

        try {
            driver.get(URL);
            Thread.sleep(1000);

            // Locate the student you want to update by email
            WebElement studentToUpdate = driver.findElement(By.xpath("//tr[td='" + TEST_STUDENT_EMAIL + "']"));

            // Locate and click the "Edit" button for the student
            studentToUpdate.findElement(By.id("editStudent")).click();
            Thread.sleep(1000);

            // Update the student's name
            driver.findElement(id("studentName")).clear();
            driver.findElement(id("studentName")).sendKeys("Updated Student Name");

            // Save the changes
            driver.findElement(id("update")).click();
            Thread.sleep(1000);

            // Verify that the student information is updated
            WebElement updatedStudent = driver.findElement(By.xpath("//tr[td='Updated Student Name']"));
            assertNotNull(updatedStudent, "Updated student not found in the student list.");

        } catch (Exception ex) {
            throw ex;
        } finally {
            driver.quit();
        }
    }

    @Test
    public void deleteStudentTest() throws Exception {
        System.setProperty("webdriver.chrome.driver", CHROME_DRIVER_FILE_LOCATION);
        WebDriver driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

        try {
            driver.get(URL);
            Thread.sleep(1000);

            // Locate the student you want to delete by email
            WebElement studentToDelete = driver.findElement(By.xpath("//tr[td='" + TEST_STUDENT_EMAIL + "']"));

            // Locate and click the "Delete" button for the student
            studentToDelete.findElement(By.id("deleteStudent")).click();
            Thread.sleep(1000);

            // Handle the confirmation dialog (if any)
            driver.switchTo().alert().accept();

            // Verify that the student is deleted
            Thread.sleep(1000);
            assertThrows(NoSuchElementException.class, () -> {
                driver.findElement(By.xpath("//tr[td='" + TEST_STUDENT_EMAIL + "']"));
            });

        } catch (Exception ex) {
            throw ex;
        } finally {
            driver.quit();
        }
    }
}