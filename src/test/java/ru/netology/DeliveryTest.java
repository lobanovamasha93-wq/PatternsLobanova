package ru.netology;

import com.codeborne.selenide.WebDriverRunner;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.Allure;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

import java.io.ByteArrayInputStream;
import java.time.Duration;

import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static org.openqa.selenium.Keys.BACK_SPACE;
import static org.openqa.selenium.Keys.CONTROL;
import static org.openqa.selenium.Keys.chord;

@Epic("Card Delivery Service")
@Feature("Meeting Scheduling")
public class DeliveryTest {

    @BeforeAll
    static void setupAllure() {
        SelenideLogger.addListener("allure", new AllureSelenide()
                .screenshots(true)
                .savePageSource(false));
    }

    @BeforeEach
    void setup() {
        open("http://localhost:9999");
    }

    @Test
    @Story("Plan and Replan Meeting")
    @Description("User fills in the form, plans a meeting for date+4, then replans to date+7 via the replan dialog")
    void shouldSuccessfulPlanAndReplanMeeting() {
        var validUser = DataGenerator.Registration.generateUser();
        var firstMeetingDate = DataGenerator.generateDate(4);
        var secondMeetingDate = DataGenerator.generateDate(7);

        Allure.step("Fill in city, first meeting date, name and phone", () -> {
            $("[data-test-id='city'] input").setValue(validUser.getCity());
            $("[data-test-id='date'] input").sendKeys(chord(CONTROL, "a"), BACK_SPACE);
            $("[data-test-id='date'] input").setValue(firstMeetingDate);
            $("[data-test-id='name'] input").setValue(validUser.getName());
            $("[data-test-id='phone'] input").setValue(validUser.getPhone());
            $("[data-test-id='agreement']").click();
            attachScreenshot("Form filled in");
        });

        Allure.step("Submit form and verify first meeting is scheduled on " + firstMeetingDate, () -> {
            $$("button").find(exactText("Запланировать")).click();
            $("[data-test-id='success-notification']")
                    .shouldBe(visible, Duration.ofSeconds(15))
                    .shouldHave(text("Встреча успешно запланирована на " + firstMeetingDate));
            attachScreenshot("Success notification – first meeting");
        });

        Allure.step("Change date to " + secondMeetingDate + " and submit again", () -> {
            $("[data-test-id='date'] input").sendKeys(chord(CONTROL, "a"), BACK_SPACE);
            $("[data-test-id='date'] input").setValue(secondMeetingDate);
            $$("button").find(exactText("Запланировать")).click();
            attachScreenshot("Replan dialog appeared");
        });

        Allure.step("Confirm replan in dialog and verify meeting is rescheduled to " + secondMeetingDate, () -> {
            $("[data-test-id='replan-notification']")
                    .shouldBe(visible, Duration.ofSeconds(15))
                    .shouldHave(text("У вас уже запланирована встреча на другую дату. Перепланировать?"));
            $$("[data-test-id='replan-notification'] button").find(exactText("Перепланировать")).click();
            $("[data-test-id='success-notification']")
                    .shouldBe(visible, Duration.ofSeconds(15))
                    .shouldHave(text("Встреча успешно запланирована на " + secondMeetingDate));
            attachScreenshot("Success notification – second meeting");
        });
    }

    private static void attachScreenshot(String name) {
        try {
            byte[] bytes = ((TakesScreenshot) WebDriverRunner.getWebDriver())
                    .getScreenshotAs(OutputType.BYTES);
            Allure.addAttachment(name, "image/png", new ByteArrayInputStream(bytes), "png");
        } catch (Exception ignored) {
        }
    }
}
