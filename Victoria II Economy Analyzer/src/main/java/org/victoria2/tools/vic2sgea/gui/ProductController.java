package org.victoria2.tools.vic2sgea.gui;

import javafx.scene.chart.PieChart;
import org.victoria2.tools.vic2sgea.entities.Country;
import org.victoria2.tools.vic2sgea.entities.Product;
import org.victoria2.tools.vic2sgea.entities.ProductStorage;
import org.victoria2.tools.vic2sgea.main.Report;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class ProductController extends ChartsController {
    private final Product product;

    private void addUniChart(Function<ProductStorage, Float> getter, String name) {
        List<ChartSlice> slices = new ArrayList<>();

        float total = 0;
        float totalSum;
        for (Country country : report.getCountryList()) {
            if (country.getTag().equals(Report.TOTAL_TAG))
                continue;
            ProductStorage productStorage = country.findStorage(product);
            if (productStorage == null)
                continue;
            float value = getter.apply(productStorage);
            total += value;
        }

        for (Country country : report.getCountryList()) {
            if (country.getTag().equals(Report.TOTAL_TAG))
                continue;
            ProductStorage productStorage = country.findStorage(product);
            if (productStorage == null)
                continue;

            float value = getter.apply(productStorage);

            slices.add(new ChartSlice(country.getTag() + String.format("(%2.1f)%%", value/total*100), value));
        }

        totalSum = total * product.price;
        String title = String.format("%s (%,.1f 제품, %,.1f£)", name , total, totalSum);

        Function<PieChart.Data, String> onEnter = data ->{
        	String countryname = data.getName().substring(0, data.getName().indexOf("("));
        	return String.format("%s: %,.2f 제품, %,.2f£",report.getCountry(countryname).getOfficialName(), data.getPieValue(), data.getPieValue() * product.price);
        };
                
        Consumer<PieChart.Data> onClick = data ->{
        	String countryname = data.getName().substring(0, data.getName().indexOf("("));
            Main.showCountry(report, report.getCountry(countryname));
        };
        
                addChart(slices, title, onEnter, onClick);
    }

    ProductController(final Report report, Product product) {
        super(report, product.getRealName());
        this.product = product;
        addUniChart(ProductStorage::getGdp, "GDP");
        addUniChart(ProductStorage::getBought, "소비");
        addUniChart(ProductStorage::getExported, "수출");
        
        addUniChart(ProductStorage::getTotalSupply, "총 공급");
        addUniChart(ProductStorage::getSold, "실 공급");
        addUniChart(ProductStorage::getImported, "수입");

    }
}
