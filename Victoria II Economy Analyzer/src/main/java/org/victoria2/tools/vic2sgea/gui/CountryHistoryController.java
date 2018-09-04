package org.victoria2.tools.vic2sgea.gui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;
import java.util.function.Consumer;
import java.util.function.Function;

import org.victoria2.tools.vic2sgea.entities.Country;
import org.victoria2.tools.vic2sgea.entities.History;
import org.victoria2.tools.vic2sgea.main.Report;

import javafx.scene.chart.XYChart;

public class CountryHistoryController extends LineChartsController {

	List<String> chartNames = new ArrayList<>();
	List<Function<Country, Float>> chartFunctions = new ArrayList<>();

	private String countryName;
	private boolean isplayer;

	public CountryHistoryController(History history, String countryName) {
		super(history, countryName + " 통계");
		
		for(Report report:history.getReportAll().values())
		{
			if(report.getPlayerCountry().getOfficialName().matches(countryName))
			{
				isplayer = true;
				break;
			}
		}
		
		this.countryName = countryName;
		
		addUniChart(Country::getPopulationf, "인구(명)");
		addUniChart(Country::getGdp, "GDP(£)");
		addUniChart(Country::getGdpPerCapita, "1인당 GDP(£)");
		addUniChart(Country::getWorkforceRgof, "RGO 인력(명)");
		addUniChart(Country::getWorkforceFactoryf, "공장 인력(명)");
		addUniChart(Country::getUnemploymentRate, "실업률(%)");
		addUniChart(Country::getLiteracy, "식자율(%)");
		
		addCharts();
	}

	private void addUniChart(Function<Country, Float> getter, String chartName) {
		chartNames.add(chartName);
		chartFunctions.add(getter);
	}

	private void addCharts() {
		List<List<ChartSlice>> charts = new ArrayList<>();

		for (int i = 0; i < chartNames.size(); i++) {
			charts.add(new ArrayList<>());
			charts.get(i).add(new ChartSlice(countryName));
		}

		TreeMap<String, Report> tm = new TreeMap<>(history.getReportAll());
		Iterator<String> key = tm.keySet().iterator();
		if(!isplayer)
		{
			while (key.hasNext()) {
				Report report = history.getReport(key.next());
				for (Country country : report.getCountryList()) {
					if (country.getOfficialName().matches(countryName)) {
						for (int i = 0; i < chartNames.size(); i++) {
							charts.get(i).get(0).addData(report.getCurrentDate(), chartFunctions.get(i).apply(country));

						}
						break;
					}
				}
			}
		}
		else
		{
			while (key.hasNext()) {
				Report report = history.getReport(key.next());
				for (Country country : report.getCountryList()) {
					if (country.getOfficialName().matches(report.getPlayerCountry().getOfficialName())) {
						for (int i = 0; i < chartNames.size(); i++) {
							charts.get(i).get(0).addData(report.getCurrentDate(), chartFunctions.get(i).apply(country));

						}
						break;
					}
				}
			}
		}
		

		Consumer<XYChart.Series<String, Double>> onClick = data -> {
			System.out.println(data.getName());
		};

		Function<XYChart.Series<String, Double>, String> onEnter = data -> {
			return String.format("%s", data.getName());
		};
		for (int i = 0; i < chartNames.size(); i++)
			addChart(charts.get(i), chartNames.get(i), 0, 1, onEnter, onClick);
	}

}
