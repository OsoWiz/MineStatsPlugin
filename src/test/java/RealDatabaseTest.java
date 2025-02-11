import dev.osowiz.speedrunstats.SpeedrunDB;
import org.junit.Assert;
import org.junit.Before;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public abstract class RealDatabaseTest {

    protected SpeedrunDB database;

    @Before
    public void setupDatabase()
    {
        // Load the file from src/test/resources
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource(".test-credentials").getFile());
        String connectionString = "";
        try {
            connectionString = new String(Files.readAllBytes(Paths.get(file.getAbsolutePath())));
        } catch (IOException e) {
            e.printStackTrace();
        }
        database = new SpeedrunDB(connectionString);
        Assert.assertNotNull(database);
    }

}
