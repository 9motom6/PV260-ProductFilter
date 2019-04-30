import cz.muni.fi.pv260.productfilter.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.fail;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ControllerTest {

    private Input input;
    private Output output;
    private Logger logger;
    private Filter<Product> blackColorFilter;
    private Filter<Product> atLeastFiveFilter;
    private Filter<Product> priceLessThanThousandFilter;
    private Product blackDog;
    private Product blueCat;
    private Product yellowMouse;
    private Product blackCat;

    @Before
    public void beforeMethod() {
        input = Mockito.mock(Input.class);
        output = Mockito.mock(Output.class);
        logger = Mockito.mock(Logger.class);

        blackDog = new Product(0, "Dog", Color.BLACK, BigDecimal.valueOf(500));
        blackCat = new Product(0, "Cat Black", Color.BLACK, BigDecimal.valueOf(1000));
        blueCat = new Product(0, "Cat", Color.BLUE, BigDecimal.valueOf(1000));
        yellowMouse = new Product(0, "Mouse", Color.YELLOW, BigDecimal.valueOf(840));
        blackColorFilter = new ColorFilter(Color.BLACK);
        priceLessThanThousandFilter = new PriceLessThanFilter(BigDecimal.valueOf(1000));
        atLeastFiveFilter = new AtLeastNOfFilter<>(2,blackColorFilter,priceLessThanThousandFilter);

    }

    @Test
    public void select_selectWithBlackFilter_oneItemResultSuccess() {
        List<Product> products = new ArrayList<>();
        products.add(blackDog);
        products.add(blueCat);
        products.add(yellowMouse);
        try {
            when(input.obtainProducts()).thenReturn(products);
        } catch (ObtainFailedException e) {
            e.printStackTrace();
            fail("An error occurred when creating input mock");
        }

        Controller controller = new Controller(input, output, logger);
        controller.select(blackColorFilter);

        Mockito.verify(output).postSelectedProducts(new ArrayList<>(Collections.singletonList(blackDog)));
    }

    @Test
    public void select_selectWithBlackFilter_twoItemResultSuccess() {
        List<Product> products = new ArrayList<>();
        products.add(blackDog);
        products.add(blackCat);
        products.add(blueCat);
        products.add(yellowMouse);
        try {
            when(input.obtainProducts()).thenReturn(products);
        } catch (ObtainFailedException e) {
            e.printStackTrace();
            fail("An error occurred when creating input mock");
        }

        Controller controller = new Controller(input, output, logger);
        controller.select(blackColorFilter);

        Mockito.verify(output).postSelectedProducts(new ArrayList<>(Arrays.asList(blackDog, blackCat)));
    }
}
