package sit707_week7;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class BodyTemperatureMonitorTest {

    private BodyTemperatureMonitor bodyTemperatureMonitor;
    private TemperatureSensor temperatureSensor;
    private CloudService cloudService;
    private NotificationSender notificationSender;

    @Before
    public void setup() {
        temperatureSensor = mock(TemperatureSensor.class);
        cloudService = mock(CloudService.class);
        notificationSender = mock(NotificationSender.class);

        bodyTemperatureMonitor = new BodyTemperatureMonitor(temperatureSensor, cloudService, notificationSender);
    }

    @Test
    public void testReadTemperature() {
        // Arrange
        double expectedTemperature = 98.6;
        when(temperatureSensor.readTemperatureValue()).thenReturn(expectedTemperature);

        // Act
        double actualTemperature = bodyTemperatureMonitor.readTemperature();

        // Assert
        assertEquals(expectedTemperature, actualTemperature, 0.001);
    }

    @Test
    public void testReportTemperatureReadingToCloud() {
        // Arrange
        double temperatureValue = 98.6;
        TemperatureReading temperatureReading = createTemperatureReading(temperatureValue);

        // Act
        bodyTemperatureMonitor.reportTemperatureReadingToCloud(temperatureReading);

        // Assert
        verify(cloudService).sendTemperatureToCloud(temperatureReading);
    }

    @Test
    public void testInquireBodyStatus_Normal() {
        // Arrange
        when(cloudService.queryCustomerBodyStatus(any(Customer.class))).thenReturn("NORMAL");

        // Act
        bodyTemperatureMonitor.inquireBodyStatus();

        // Assert
        verify(notificationSender).sendEmailNotification(any(Customer.class), eq("Thumbs Up!"));
    }

    @Test
    public void testInquireBodyStatus_Abnormal() {
        // Arrange
        when(cloudService.queryCustomerBodyStatus(any(Customer.class))).thenReturn("ABNORMAL");

        // Act
        bodyTemperatureMonitor.inquireBodyStatus();

        // Assert
        verify(notificationSender).sendEmailNotification(any(FamilyDoctor.class), eq("Emergency!"));
    }

    // Helper method to create a TemperatureReading object with a specific value
    private TemperatureReading createTemperatureReading(double value) {
        TemperatureReading temperatureReading = new TemperatureReading();
        temperatureReading.setValue(value);
        return temperatureReading;
    }
}
