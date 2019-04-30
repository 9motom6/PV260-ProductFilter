import cz.muni.fi.pv260.productfilter.AtLeastNOfFilter;
import cz.muni.fi.pv260.productfilter.Filter;
import cz.muni.fi.pv260.productfilter.FilterNeverSucceeds;
import cz.muni.fi.pv260.productfilter.Product;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;

@SuppressWarnings("unchecked")
@RunWith(MockitoJUnitRunner.class)
public class AtLeastNOfFilterTest {
    private Filter filterPassing;
    private Filter filterFailing;
    @Before
    public void beforeMethod() {
        filterPassing = Mockito.mock(Filter.class);
        doReturn(true).when(filterPassing).passes(null);

        filterFailing = Mockito.mock(Filter.class);
        doReturn(false).when(filterFailing).passes(null);
    }

    @Test
    public void AtLeastNOfFilter_noExceptionsThrown_validInput() {
        new AtLeastNOfFilter(1, Mockito.mock(Filter.class) );
    }

    @Test(expected = FilterNeverSucceeds.class)
    public void AtLeastNOfFilter_throwFilterNeverSucceeds_ifnIsTooHigh() {
        new AtLeastNOfFilter(5, Mockito.mock(Filter.class) );
    }

    @Test(expected = IllegalArgumentException.class)
    public void AtLeastNOfFilter_throwIllegalArgumentException_ifnIsZero() {
        new AtLeastNOfFilter(0, Mockito.mock(Filter.class) );
    }

    @Test(expected = IllegalArgumentException.class)
    public void AtLeastNOfFilter_throwIllegalArgumentException_ifnIsNegative() {
        new AtLeastNOfFilter(-1, Mockito.mock(Filter.class) );
        new AtLeastNOfFilter(-15, Mockito.mock(Filter.class) );
        new AtLeastNOfFilter(Integer.MIN_VALUE, Mockito.mock(Filter.class) );
    }

    @Test
    public void passes_filterPasses_manyChildrenPass() {
        AtLeastNOfFilter<Product> atLeastNOfFilter = new AtLeastNOfFilter(1, filterPassing, filterPassing );

        assertTrue(atLeastNOfFilter.passes(null));
    }

    @Test
    public void passes_filterPasses_nChildrenPass() {
        AtLeastNOfFilter<Product> atLeastNOfFilter = new AtLeastNOfFilter(1, filterPassing );

        assertTrue(atLeastNOfFilter.passes(null));
    }

    @Test
    public void passes_filterDoesNotPass_noChildrenPass() {
        AtLeastNOfFilter<Product> atLeastNOfFilter = new AtLeastNOfFilter(1, filterFailing );

        assertFalse(atLeastNOfFilter.passes(null));
    }

    @Test
    public void passes_filterDoesNotPass_notEnoughChildrenPass() {
        MockFilter mockFilter = new MockFilter();

        AtLeastNOfFilter<Product> atLeastNOfFilter = new AtLeastNOfFilter(2, filterFailing, mockFilter );

        assertFalse(atLeastNOfFilter.passes(null));
    }

    private class MockFilter implements Filter<Object> {
        @Override
        public boolean passes(Object item) {
            return true;
        }
    }
}