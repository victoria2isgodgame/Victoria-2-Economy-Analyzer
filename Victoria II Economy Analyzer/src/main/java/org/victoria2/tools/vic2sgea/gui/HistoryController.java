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

public class HistoryController extends LineChartsController {

	List<String> chartNames = new ArrayList<>();
	List<Function<Country, Float>> chartFunctions = new ArrayList<>();

	public HistoryController(History history) {
		super(history, "통계");

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
		
		/*List<ChartSlice> slices = new ArrayList<ChartSlice>();

		List<String> dates = new ArrayList<>();
		TreeMap<String, Report> tm = new TreeMap<>(history.getReportAll());
		Iterator<String> key = tm.keySet().iterator();
		List<ChartSlice> player = new ArrayList<ChartSlice>();
		while (key.hasNext()) {
			String date = key.next();
			dates.add(date);
			Report report = history.getReport(date);
			for (Country country : report.getCountryList()) {
				if (country.getTag().matches("TOT"))
					continue;
				boolean exist = false;
				for (ChartSlice slice : slices) {
					if (slice.name.matches(country.getOfficialName())) {
						slice.addData(date, getter.apply(country));
						exist = true;
						break;
					}
				}
				if (!exist) {
					ChartSlice slice = new ChartSlice(country.getOfficialName());
					slice.addData(date, getter.apply(country));
					slices.add(slice);
					if (slice.getName().matches(report.getPlayerCountry().getOfficialName()))
						player.add(slice);

				}
			}

		}
		ChartSlice playerslice = new ChartSlice(player.get(player.size() - 1).getName());
		for (ChartSlice slice : player) {
			for (XYChart.Data<String, Double> data : slice.getData().getData()) {
				playerslice.addData(data.getXValue(), data.getYValue());
			}
			slices.remove(slice);
		}
		slices.add(playerslice);

		for (ChartSlice slice : slices) {
			if (slice.getData().getData().size() != history.getReportAll().size()) {
				int cnt = 0;
				for (String date : dates) {
					if (slice.getData().getData().get(cnt).getXValue() != date) {
						slice.getData().getData().add(cnt++, new XYChart.Data<String, Double>(date, 0d));
					} else
						break;
				}
			}
		}

		// slices.sort(Comparator.comparing(ChartSlice::getName));

		Consumer<XYChart.Series<String, Double>> onClick = data -> {
			System.out.println(data.getName());
		};

		Function<XYChart.Series<String, Double>, String> onEnter = data -> {
			return String.format("%s", data.getName());
		};

		String title = chartName;
		addChart(slices, title, onEnter, onClick);*/
	}

	private void addCharts() {
		
		
		
		List<List<ChartSlice>> charts = new ArrayList<>();
		List<List<ChartSlice>> playercharts = new ArrayList<>();

		for (int i = 0; i < chartNames.size(); i++) {
			charts.add(new ArrayList<>());
			playercharts.add(new ArrayList<>());
		}

		List<String> dates = new ArrayList<>();
		TreeMap<String, Report> tm = new TreeMap<>(history.getReportAll());
		Iterator<String> key = tm.keySet().iterator();
		while (key.hasNext()) {
			String date = key.next();
			dates.add(date);
			Report report = history.getReport(date);
			for (Country country : report.getCountryList()) {
				if (country.getTag().matches("TOT"))
					continue;
				boolean exist = false;
				for (int i = 0; i < charts.get(0).size(); i++) {
					ChartSlice slice = charts.get(0).get(i);
					if (slice.name.matches(country.getOfficialName())) {
						for (int j = 0; j < chartNames.size(); j++) {
							charts.get(j).get(i).addData(date, chartFunctions.get(j).apply(country));
						}
						exist = true;
						break;
					}
				}
				if (!exist) {
					for (int j = 0; j < chartNames.size(); j++) {
						ChartSlice slice = new ChartSlice(country.getOfficialName()); 
						slice.addData(date, chartFunctions.get(j).apply(country));
						charts.get(j).add(slice);
						if (slice.getName().matches(report.getPlayerCountry().getOfficialName()))
							playercharts.get(j).add(slice);
					}
					

				}
			}

		}
		for(int i = 0; i<chartNames.size(); i++)
		{
			ChartSlice playerslice = new ChartSlice(playercharts.get(0).get(playercharts.get(0).size()-1).getName());
			for (ChartSlice slice : playercharts.get(i)) {
				for (XYChart.Data<String, Double> data : slice.getData().getData()) {
					playerslice.addData(data.getXValue(), data.getYValue());
				}
				charts.get(i).remove(slice);
			}
			charts.get(i).add(playerslice);
		}
		
		
		Consumer<XYChart.Series<String, Double>> onClick = data -> {
			Main.showCountryHistoryWindow(history, data.getName());
		};

		Function<XYChart.Series<String, Double>, String> onEnter = data -> {
			return String.format("%s", data.getName());
		};
		
		
		for (int i = 0; i<chartNames.size(); i++)
		{
			for (ChartSlice slice : charts.get(i)) {
				if (slice.getData().getData().size() != history.getReportAll().size()) {
					int cnt = 0;
					for (String date : dates) {
						if (slice.getData().getData().get(cnt).getXValue() != date) {
							slice.getData().getData().add(cnt++, new XYChart.Data<String, Double>(date, 0d));
						} else
						{
							if(cnt != 0)
								slice.getData().getData().add(cnt++, new XYChart.Data<String, Double>(date, 0d));
							break;
						}
							
					}
				}
			}
			addChart(charts.get(i), chartNames.get(i), 0, 10, onEnter, onClick);
		}

		// slices.sort(Comparator.comparing(ChartSlice::getName));

	}
}
