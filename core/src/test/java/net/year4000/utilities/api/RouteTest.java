package net.year4000.utilities.api;

import junit.framework.Assert;
import lombok.extern.java.Log;
import net.year4000.utilities.api.routes.accounts.AccountRoute;
import org.junit.Test;

@Log
public class RouteTest {
    @Test
    public void accountTest() {
        AccountRoute response = API.getAccount("54c572bba6946f1b42c0bd0e");
        Assert.assertEquals(response.getUsername(), "Year4000");
        Assert.assertEquals(response.getUUID(), "96e51f12-2c2f-42a6-a2d0-045d1eb4b5b2");
    }

    @Test
    public void accountAsyncTest() {
        API.getAccountAsync("54c572bba6946f1b42c0bd0e", (response, error) -> {
            if (error != null) {
                throw new RuntimeException(error);
            }

            Assert.assertEquals(response.getUsername(), "Year4000");
            Assert.assertEquals(response.getUUID(), "96e51f12-2c2f-42a6-a2d0-045d1eb4b5b2");
        });
    }
}
