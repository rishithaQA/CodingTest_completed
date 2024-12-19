package com.example.walmart

import android.app.Application
import com.example.walmart.data.di.dataModule
import com.example.walmart.data.di.networkModule
import com.example.walmart.domain.di.ServiceProvider
import com.example.walmart.domain.di.add
import com.example.walmart.domain.di.module
import com.example.walmart.presentation.di.presentationModule
import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import java.util.Arrays;
import java.util.List;
import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Observer;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.verify;

class WalmartApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        ServiceProvider.initialize(
            presentationModule,
            dataModule,
            networkModule,
            // add application context dependency
            module { add { applicationContext } }
        )
    }

//This test will check the SearchCountryUseCase for different cases:
//Empty search returns all countries.
//Exact match returns the correct country.
//No match returns an empty list.

@RunWith(Parameterized.class)
public class SearchCountryUseCaseTest {

    private final String searchQuery;
    private final List<String> expectedCountries;

    public SearchCountryUseCaseTest(String searchQuery, List<String> expectedCountries) {
        this.searchQuery = searchQuery;
        this.expectedCountries = expectedCountries;
    }

    @Parameterized.Parameters
    public static Iterable<Object[]> data() {
        return Arrays.asList(new Object[][]{
            {"", Arrays.asList("UK", "France", "Germany")},  // All countries
            {"UK", Arrays.asList("UK")},                  // One country match
            {"abcdetest", Arrays.asList()},                        // No match
        });
    }

    @Test
    public void testSearchCountry() {
        SearchCountryUseCase useCase = new SearchCountryUseCase();
        List<String> result = useCase.searchCountries(searchQuery);
        assertEquals(expectedCountries, result);
    }
}

public class CountriesViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private CountriesViewModel viewModel;

    @Mock
    private Observer<List<String>> observer;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        viewModel = new CountriesViewModel();
        viewModel.getCountries().observeForever(observer);
    }

    @Test
    public void testSearch() {
        String query = "UK";
        viewModel.searchCountries(query);
        verify(observer).onChanged(Arrays.asList("UK"));  // Expect that search returns UK
    }
}
}