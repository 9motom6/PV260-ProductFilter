import cz.muni.fi.pv260.productfilter.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Collection;

import static org.assertj.core.api.Assertions.fail;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ControllerTest {

    private Input input;
    private Output output;
    private Logger logger;

    private Filter<Product> blackColorFilter;
    private Filter<Product> whiteColorFilter;
    private Filter<Product> priceLessThanThousandFilter;
    private Filter<Product> atLeastTwoFilter;

    private Product blackDogFor500;
    private Product yellowMouseFor840;
    private Product blackCatFor1001;

    private List<Product> products;
    private Controller controller;

    @Before
    public void beforeMethod() {
        input = Mockito.mock(Input.class);
        output = Mockito.mock(Output.class);
        logger = Mockito.mock(Logger.class);
        products = new ArrayList<>();

        blackDogFor500 = new Product(0, "Dog", Color.BLACK, BigDecimal.valueOf(500));
        blackCatFor1001 = new Product(0, "Cat Black", Color.BLACK, BigDecimal.valueOf(1001));
        Product blueCatFor1010 = new Product(0, "Cat", Color.BLUE, BigDecimal.valueOf(1010));
        yellowMouseFor840 = new Product(0, "Mouse", Color.YELLOW, BigDecimal.valueOf(840));
        blackColorFilter = new ColorFilter(Color.BLACK);
        whiteColorFilter = new ColorFilter(Color.WHITE);
        priceLessThanThousandFilter = new PriceLessThanFilter(BigDecimal.valueOf(1000));
        atLeastTwoFilter = new AtLeastNOfFilter<>(2, blackColorFilter, priceLessThanThousandFilter);

        products.add(blackDogFor500);
        products.add(blueCatFor1010);
        products.add(yellowMouseFor840);
        products.add(blackCatFor1001);

        controller = new Controller(input, output, logger);

        try {
            when(input.obtainProducts()).thenReturn(products);
        } catch (ObtainFailedException e) {
            e.printStackTrace();
            fail("An error occurred when creating input mock");
        }

    }

    //================================First subtask tests
    @Test
    public void select_selectWithBlackFilter_oneItemResultSuccess() {
        products.remove(blackCatFor1001);

        controller.select(blackColorFilter);

        Mockito.verify(output).postSelectedProducts(new ArrayList<>(Collections.singletonList(blackDogFor500)));
    }

    @Test
    public void select_selectWithPriceLessThatThousandFilter_twoItemsResultSuccess() {
        controller.select(priceLessThanThousandFilter);

        Mockito.verify(output).postSelectedProducts(new ArrayList<>(Arrays.asList(blackDogFor500, yellowMouseFor840)));
    }

    @Test
    public void select_selectWithPriceLessThatThousandFilter_zeroItemsResultSuccess() {
        products.remove(blackDogFor500);
        products.remove(yellowMouseFor840);


        controller.select(priceLessThanThousandFilter);

        Mockito.verify(output).postSelectedProducts(new ArrayList<>(Collections.<Product>emptyList()));
    }

    @Test
    public void select_selectWithAtLeastTwoFiltersBlackAndLessThanThousand_oneItemResultSuccess() {
        controller.select(atLeastTwoFilter);

        Mockito.verify(output).postSelectedProducts(new ArrayList<>(Collections.singletonList(blackDogFor500)));
    }

    @Test
    public void select_selectWithAtLeastTwoFilterBlackAndLessThanThousand_zeroItemsResultSuccess() {
        products.remove(blackDogFor500);

        controller.select(atLeastTwoFilter);

        Mockito.verify(output).postSelectedProducts(new ArrayList<>(Collections.<Product>emptyList()));
    }

    @Test
    public void select_selectWithBlackFilter_twoItemResultSuccess() {
        controller.select(blackColorFilter);

        Mockito.verify(output).postSelectedProducts(new ArrayList<>(Arrays.asList(blackDogFor500, blackCatFor1001)));
    }

    @Test
    public void select_selectWithWhiteFilter_emptyResultSuccess() {
        controller.select(whiteColorFilter);

        Mockito.verify(output).postSelectedProducts(new ArrayList<>(Collections.<Product>emptyList()));
    }

    @Test
    public void select_selectWithWhiteFilter_oneItemResultSuccess() {
        Input input = new DummyInput();
        Controller controller = new Controller(input,output,logger);
        controller.select(whiteColorFilter);

        Mockito.verify(output).postSelectedProducts(new ArrayList<>(Collections.singletonList(new Product(99, "Pencil", Color.WHITE, BigDecimal.valueOf(500)))));
    }

    //================================Second subtask tests
    @Test
    public void select_selectWithBlackFilter_correctLogMessageOnSuccess() {
        controller.select(blackColorFilter);

        Mockito.verify(logger).log(Controller.TAG_CONTROLLER, "Successfully selected 2 out of 4 available products.");
    }

    @Test
    public void select_selectWithWhiteFilterZeroResultSize_correctLogMessageOnSuccess() {
        controller.select(whiteColorFilter);

        Mockito.verify(logger).setLevel("INFO");
        Mockito.verify(logger).log(Controller.TAG_CONTROLLER, "Successfully selected 0 out of 4 available products.");
    }

    //================================Third subtask test

    @Test
    public void select_selectWithBlackFilterThrowException_exceptionLogged() {
        try {
            when(input.obtainProducts()).thenThrow(ObtainFailedException.class);
        } catch (ObtainFailedException e) {
            e.printStackTrace();
            fail("An error occurred when creating input mock");
        }

        controller.select(whiteColorFilter);


        Mockito.verify(logger).setLevel("ERROR");
        Mockito.verify(logger).log(Controller.TAG_CONTROLLER, "Filter procedure failed with exception: " + ObtainFailedException.class.getName());
    }

    //================================Fourth subtask test

    @Test
    public void select_selectWithBlackFilterThrowException_nothingPassedToOutput() {
        try {
            when(input.obtainProducts()).thenThrow(ObtainFailedException.class);
        } catch (ObtainFailedException e) {
            e.printStackTrace();
            fail("An error occurred when creating input mock");
        }

        controller.select(whiteColorFilter);

        Mockito.verify(output, never()).postSelectedProducts(Matchers.<Collection<Product>>any());
    }


    private class DummyInput implements Input{
        @Override
        public Collection<Product> obtainProducts(){
            return Collections.singletonList(new Product(99, "Pencil", Color.WHITE, BigDecimal.valueOf(500)));
        }
    }


}
