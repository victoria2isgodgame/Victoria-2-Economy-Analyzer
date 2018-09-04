package org.victoria2.tools.vic2sgea.gui;

import javafx.event.ActionEvent;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import org.victoria2.tools.vic2sgea.entities.Country;
import org.victoria2.tools.vic2sgea.entities.EconomySubject;
import org.victoria2.tools.vic2sgea.entities.Product;
import org.victoria2.tools.vic2sgea.entities.ProductStorage;
import org.victoria2.tools.vic2sgea.main.Report;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CountryController extends ChartsController {
	private final Country country;
	private final Map<String, ProductStorage> storageMap;

	private void addUniChart(Function<EconomySubject, Float> getter, String chartName) {
		List<ChartSlice> slices = storageMap.values().stream()
				.filter(productStorage -> getter.apply(productStorage) > 0).map(productStorage -> {
					String name = productStorage.product.getRealName();
					double value = getter.apply(productStorage) * productStorage.getPrice();
					Color color = productStorage.product.getColor();

					ChartSlice slice = new ChartSlice(
							name + String.format("(%2.1f%%)", value / getter.apply(country) * 100), value, color);

					return slice;
				}).collect(Collectors.toList());

		Consumer<PieChart.Data> onClick = data -> {
			String productname = data.getName().substring(0, data.getName().indexOf("("));
			System.out.println(productname);
			Product foundProduct = report.findProduct(report.localizedproducts.get(productname));
			if (foundProduct != null) {
				Main.showProduct(report, foundProduct);
			}
		};

		Function<PieChart.Data, String> onEnter = data -> {
			ProductStorage productStorage = null;
			Set<Entry<String, ProductStorage>> list = storageMap.entrySet();
			String productname = data.getName().substring(0, data.getName().indexOf("("));
			for (Map.Entry<String, ProductStorage> p : list) {
				if (p.getValue().product.getRealName().matches(productname)) {
					productStorage = p.getValue();
					break;
				}
			}
			return String.format("%s: %.1f£ (%,.1f 제품)", productname, data.getPieValue(), getter.apply(productStorage));
		};

		String title = String.format("%s (%,.1f£)", chartName, getter.apply(country));
		addChart(slices, title, onEnter, onClick);
	}

	private void addPopChart() {
		List<ChartSlice> slices = new ArrayList<ChartSlice>();
		long upperclass = country.getPopulation("aristocrats") + country.getPopulation("capitalists");
		long middleclass = country.getPopulation("bureaucrats") + country.getPopulation("artisans")
				+ country.getPopulation("clergymen") + country.getPopulation("clerks")
				+ country.getPopulation("officers");
		long underclass = country.getPopulation("craftsmen") + country.getPopulation("farmers")
				+ country.getPopulation("soldiers") + country.getPopulation("labourers")
				+ country.getPopulation("serfs") + country.getPopulation("slaves");

		/*
		*/
		slices.add(new ChartSlice(String.format("%s(%2.1f%%)", "상류층", upperclass * 1f / country.getPopulation() * 100),
				upperclass));
		slices.add(new ChartSlice(String.format("%s(%2.1f%%)", "중산층", middleclass * 1f / country.getPopulation() * 100),
				middleclass));
		slices.add(new ChartSlice(String.format("%s(%2.1f%%)", "하류층", underclass * 1f / country.getPopulation() * 100),
				underclass));

		Consumer<PieChart.Data> onClick = data -> {
			System.out.println("");
		};

		Function<PieChart.Data, String> onEnter = data -> {
			return data.getName();
		};

		addChart(slices, "계층", onEnter, onClick);

		slices = new ArrayList<>();

		slices.add(new ChartSlice(
				String.format("%s(%2.1f%%)", "지주",
						country.getPopulation("aristocrats") * 1f / country.getPopulation() * 100),
				country.getPopulation("aristocrats")));
		slices.add(new ChartSlice(
				String.format("%s(%2.1f%%)", "자본가",
						country.getPopulation("capitalists") * 1f / country.getPopulation() * 100),
				country.getPopulation("capitalists")));
		slices.add(new ChartSlice(
				String.format("%s(%2.1f%%)", "관료",
						country.getPopulation("bureaucrats") * 1f / country.getPopulation() * 100),
				country.getPopulation("bureaucrats")));
		slices.add(new ChartSlice(
				String.format("%s(%2.1f%%)", "장인",
						country.getPopulation("artisans") * 1f / country.getPopulation() * 100),
				country.getPopulation("artisans")));
		slices.add(new ChartSlice(
				String.format("%s(%2.1f%%)", "지식인",
						country.getPopulation("clergymen") * 1f / country.getPopulation() * 100),
				country.getPopulation("clergymen")));
		slices.add(new ChartSlice(
				String.format("%s(%2.1f%%)", "사무원",
						country.getPopulation("clerks") * 1f / country.getPopulation() * 100),
				country.getPopulation("clerks")));
		slices.add(new ChartSlice(
				String.format("%s(%2.1f%%)", "장교",
						country.getPopulation("officers") * 1f / country.getPopulation() * 100),
				country.getPopulation("officers")));
		slices.add(new ChartSlice(
				String.format("%s(%2.1f%%)", "직공",
						country.getPopulation("craftsmen") * 1f / country.getPopulation() * 100),
				country.getPopulation("craftsmen")));
		slices.add(new ChartSlice(
				String.format("%s(%2.1f%%)", "농부",
						country.getPopulation("farmers") * 1f / country.getPopulation() * 100),
				country.getPopulation("farmers")));
		slices.add(new ChartSlice(
				String.format("%s(%2.1f%%)", "군인",
						country.getPopulation("soldiers") * 1f / country.getPopulation() * 100),
				country.getPopulation("soldiers")));
		slices.add(new ChartSlice(
				String.format("%s(%2.1f%%)", "노동자",
						country.getPopulation("labourers") * 1f / country.getPopulation() * 100),
				country.getPopulation("labourers")));
		slices.add(new ChartSlice(
				String.format("%s(%2.1f%%)", "농노", country.getPopulation("serfs") * 1f / country.getPopulation() * 100),
				country.getPopulation("serfs")));
		slices.add(new ChartSlice(
				String.format("%s(%2.1f%%)", "노예",
						country.getPopulation("slaves") * 1f / country.getPopulation() * 100),
				country.getPopulation("slaves")));

		addChart(slices, "세부 계층", onEnter, onClick);

		long totalworkforce = country.getWorkforceFactory() + country.getWorkforceRgo();
		slices = new ArrayList<>();
		slices.add(new ChartSlice(
				String.format("%s(%2.1f%%)", "RGO", country.getWorkforceRgo() * 1f / totalworkforce * 100),
				country.getWorkforceRgo()));
		slices.add(new ChartSlice(
				String.format("%s(%2.1f%%)", "공장", country.getWorkforceFactory() * 1f / totalworkforce * 100),
				country.getWorkforceFactory()));
		addChart(slices, "노동자 비중 (" + String.format("%,d", totalworkforce) + "명)", onEnter, onClick);

		long totalunemployee = country.getWorkforceFactory() + country.getWorkforceRgo() - country.getEmployment();
		slices = new ArrayList<>();
		slices.add(new ChartSlice(
				String.format("%s(%2.1f%%)", "RGO",
						(country.getWorkforceRgo() - country.getEmploymentRGO()) * 1f / totalunemployee * 100),
				(country.getWorkforceRgo() - country.getEmploymentRGO())));
		slices.add(new ChartSlice(
				String.format("%s(%2.1f%%)", "공장",
						(country.getWorkforceFactory() - country.getEmploymentFactory()) * 1f / totalunemployee * 100),
				(country.getWorkforceFactory() - country.getEmploymentFactory())));
		addChart(slices, "실업자 비중 (" + String.format("%,d", totalunemployee) + "명)", onEnter, onClick);

		long totalfactoryworker = country.getWorkforceFactory();
		slices = new ArrayList<>();
		slices.add(new ChartSlice(
				String.format("%s(%2.1f%%)", "취업자", country.getEmploymentFactory() * 1f / totalfactoryworker * 100),
				country.getEmploymentFactory()));
		slices.add(
				new ChartSlice(
						String.format("%s(%2.1f%%)", "실업자",
								(country.getWorkforceFactory() - country.getEmploymentFactory()) * 1f
										/ totalfactoryworker * 100),
						(country.getWorkforceFactory() - country.getEmploymentFactory())));
		addChart(slices, "공장 취업률", onEnter, onClick);

		long totalrgoworker = country.getWorkforceRgo();
		slices = new ArrayList<>();
		slices.add(new ChartSlice(
				String.format("%s(%2.1f%%)", "취업자", country.getEmploymentRGO() * 1f / totalrgoworker * 100),
				country.getEmploymentRGO()));
		slices.add(new ChartSlice(
				String.format("%s(%2.1f%%)", "실업자",
						(country.getWorkforceRgo() - country.getEmploymentRGO()) * 1f / totalrgoworker * 100),
				(country.getWorkforceRgo() - country.getEmploymentRGO())));
		addChart(slices, "RGO 취업률", onEnter, onClick);

	}

	CountryController(final Report report, final Country country) {
		super(report, country.getOfficialName());
		this.country = country;
		this.storageMap = country.getStorage();

		ImageView flag0 = new ImageView(
				getClass().getResource("/flags/" + country.getTag() + country.getFlag() + ".png").toString());
		ImageView flag1 = new ImageView(
				getClass().getResource("/flags/" + country.getTag() + country.getFlag() + ".png").toString());
		Label government = new Label(country.getGovernment() + "\n인구: " + String.format("%,d", country.getPopulation())
				+ "명\n1인당 GDP: " + String.format("%,f", country.getGdpPerCapita()) + "£\n" + "식자율: "
				+ String.format("%2.2f%%", country.getLiteracy()));
		government.setAlignment(Pos.CENTER);
		government.getStyleClass().add("label-gov");
		Button historyButton = new Button("통계");
		historyButton.setPrefSize(60, 60);
		this.grid.add(flag0, 1, 1);
		this.grid.add(flag1, 1, 1);
		this.grid.add(government, 1, 1);
		this.grid.add(historyButton, 2, 1);

		GridPane.setHalignment(flag0, HPos.LEFT);
		GridPane.setHalignment(flag1, HPos.RIGHT);
		GridPane.setHalignment(government, HPos.CENTER);
		GridPane.setHalignment(historyButton, HPos.CENTER);

		historyButton.addEventHandler(ActionEvent.ACTION,
				e -> Main.showCountryHistoryWindow(WindowController.history, country.getOfficialName()));

		addUniChart(EconomySubject::getGdp, "GDP");
		addUniChart(EconomySubject::getBought, "소비");
		addUniChart(EconomySubject::getExported, "수출");

		// addUniChart(report, country, "maxDemand",2,0, "maxDemand");
		addUniChart(EconomySubject::getTotalSupply, "공급");
		addUniChart(EconomySubject::getSold, "판매");
		addUniChart(EconomySubject::getImported, "수입");

		addPopChart();

		GridPane pane = new GridPane();
		pane.setHgap(5);
		pane.setVgap(5);
		grid.add(pane, 0, 1);
		GridPane.setHalignment(pane, HPos.CENTER);
		Button economy = new Button("경제");
		Button pop = new Button("국민");
		economy.addEventHandler(ActionEvent.ACTION, e -> setPage(0));
		pop.addEventHandler(ActionEvent.ACTION, e -> setPage(1));
		pane.add(economy, 0, 0);
		pane.add(pop, 1, 0);

	}
}
