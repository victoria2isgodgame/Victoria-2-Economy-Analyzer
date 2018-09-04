package org.victoria2.tools.vic2sgea.gui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.victoria2.tools.vic2sgea.entities.History;
import org.victoria2.tools.vic2sgea.main.Wrapper;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

public class LineChartsController extends BaseController {
	protected final Scene scene;
	protected final GridPane grid;
	protected final GridPane buttonPane;
	protected final History history;
	private List<LineChart> chartList = new ArrayList<>();
	public int selectedIndex = -1;
	private AnchorPane chartPane;
	private boolean isInChartPane;
	Button prevButton;
	Button nextButton;
	Label caption;

	private int chartCount = 0;

	public LineChartsController(History history, String titlename) {
		this.history = history;
		this.grid = new GridPane();

		// grid.setAlignment(Pos.CENTER);
		grid.setAlignment(Pos.TOP_CENTER);
		grid.setHgap(5);
		grid.setVgap(5);
		grid.setPadding(new Insets(5, 5, 5, 5));
		grid.setStyle("-fx-background-color: #400000");

		ScrollPane scrollPane = new ScrollPane();
		scrollPane.setContent(grid);

		scene = new Scene(scrollPane);
		scene.getStylesheets().add("/gui/linechart.css");

		Label title = new Label(titlename);
		title.getStyleClass().add("title");
		grid.add(title, 0, 0);
		GridPane.setHalignment(title, HPos.CENTER);

		prevButton = new Button("이전");
		nextButton = new Button("다음");
		grid.add(prevButton, 0, 0);
		grid.add(nextButton, 0, 0);
		GridPane.setHalignment(prevButton, HPos.LEFT);
		GridPane.setHalignment(nextButton, HPos.RIGHT);

		buttonPane = new GridPane();
		buttonPane.setHgap(5);
		buttonPane.setVgap(5);
		grid.add(buttonPane, 0, 1);
		GridPane.setHalignment(buttonPane, HPos.CENTER);
		buttonPane.setAlignment(Pos.BASELINE_CENTER);

		prevButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				if (selectedIndex > 0)
					setChart(selectedIndex - 1);
				else
					setChart(chartList.size() - 1);

			}

		});

		nextButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				if (selectedIndex < chartList.size() - 1)
					setChart(selectedIndex + 1);
				else
					setChart(0);

			}

		});

		chartPane = new AnchorPane();

		caption = new Label("");
		caption.setAlignment(Pos.BASELINE_CENTER);
		caption.getStyleClass().add("chart-caption");
		chartPane.getChildren().add(caption);
		caption.setVisible(false);
		chartPane.setPadding(new Insets(0, 0, 0, 0));
		grid.add(chartPane, 0, 2);

		chartPane.setOnMouseMoved(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if (isInChartPane)
					setCaptionPos(event.getX(), event.getY());
			}
		});
	}

	/**
	 * Adds chart to grid
	 *
	 * @param pieChartData chart data
	 * @param title        chart title
	 * @param onEnter      function that returns caption when mouse enters an item
	 * @param onClick      consumer called when an item is clicked
	 * @return the created chart
	 */
	protected LineChart addChart(List<ChartSlice> slices, String title, int i, int j,
			Function<XYChart.Series<String, Double>, String> onEnter,
			Consumer<XYChart.Series<String, Double>> onClick) {
		final CategoryAxis xAxis = new CategoryAxis();
		final NumberAxis yAxis = new NumberAxis();

		chartCount++;

		List<XYChart.Series<String, Double>> lineChartData = slices.stream().map(chartSlice -> chartSlice.data)
				.collect(Collectors.toList());

		Collections.sort(lineChartData, new Comparator<XYChart.Series<String, Double>>() {
			@Override
			public int compare(XYChart.Series<String, Double> o1, XYChart.Series<String, Double> o2) {

				int d1 = Integer.parseInt(o1.getData().get(o1.getData().size() - 1).getXValue().replaceAll("\\.", ""));
				int d2 = Integer.parseInt(o2.getData().get(o2.getData().size() - 1).getXValue().replaceAll("\\.", ""));
				if (d1 > d2)
					return -1;
				else if (d1 < d2)
					return 1;
				else {
					double v1 = o1.getData().get(o1.getData().size() - 1).getYValue();
					double v2 = o2.getData().get(o2.getData().size() - 1).getYValue();
					if (v1 > v2)
						return -1;
					else if (v1 < v2)
						return 1;
					else
						return 0;
				}
			}
		});
		List<XYChart.Series<String, Double>> sublist = lineChartData.subList(i, j);

		final LineChart chart = new LineChart(xAxis, yAxis);

		chart.setData(FXCollections.observableList(sublist));

		chart.setMaxSize(700, 700);
		chart.setPrefSize(700, 700);
		chart.setLegendVisible(true);
		AnchorPane.setLeftAnchor(chart, 20d);
		AnchorPane.setRightAnchor(chart, 20d);
		chart.setTitle(title);
		chart.setTitleSide(Side.TOP);
		chart.setCreateSymbols(false);

		if (selectedIndex == -1) {
			chartPane.getChildren().add(chart);
			selectedIndex = 0;
		}

		chartList.add(chart);

		for (XYChart.Series<String, Double> data : sublist) {
			/*
			 * data.getNode().setOnMouseEntered(new EventHandler<MouseEvent>() {
			 * 
			 * @Override public void handle(MouseEvent event) {
			 * setCaptionText(onEnter.apply(data), event.getSceneX(), event.getSceneY()); }
			 * });
			 */
			data.getNode().addEventHandler(MouseEvent.MOUSE_ENTERED, e -> setCaptionText(onEnter.apply(data)));
			data.getNode().addEventHandler(MouseEvent.MOUSE_ENTERED,
					e -> data.getNode().setStyle("-fx-stroke-width: 5px;"));
			data.getNode().addEventHandler(MouseEvent.MOUSE_EXITED,
					e -> data.getNode().setStyle("-fx-stroke-width: 3px;"));
			data.getNode().addEventHandler(MouseEvent.MOUSE_EXITED, e -> caption.setVisible(false));
			data.getNode().addEventHandler(MouseEvent.MOUSE_EXITED, e -> caption.setText(""));
			data.getNode().addEventHandler(MouseEvent.MOUSE_CLICKED, e -> onClick.accept(data));
		}

		chart.setOnMouseEntered(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent arg0) {
				isInChartPane = true;
			}

		});
		chart.setOnMouseExited(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				isInChartPane = false;
			}

		});

		Button button = new Button(title);
		button.setPrefSize(100, 20);
		int cnt = chartList.size() - 1;
		button.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				setChart(cnt);
			}

		});

		buttonPane.add(button, cnt % 5, cnt / 5);
		GridPane.setHalignment(button, HPos.CENTER);

		return chart;
	}

	public void setChart(int index) {
		chartPane.getChildren().remove(chartList.get(selectedIndex));
		chartPane.getChildren().add(chartList.get(index));
		selectedIndex = index;
	}

	private void setCaptionText(String text) {
		caption.setVisible(true);
		caption.setText(text);

		caption.toFront();
	}

	private void setCaptionPos(double posX, double posY) {
		caption.setLayoutX(posX - caption.getText().length() * 6);
		caption.setLayoutY(posY - 30);
	}

	public Scene getScene() {
		return scene;
	}

	static class ChartSlice {
		public String name;
		private Color color;
		private XYChart.Series<String, Double> data;

		public ChartSlice(String name, Color color) {
			this(name);
			this.color = color;
		}

		public ChartSlice(String name) {
			this.name = name;
			data = new XYChart.Series<String, Double>();
			data.setName(name);
		}

		public String getName() {
			return name;
		}

		public void addData(String date, double value) {
			data.getData().add(new XYChart.Data<String, Double>(date, value));
		}

		public XYChart.Series<String, Double> getData() {
			return data;
		}

		void setColor() {
			if (color == null)
				return;
			String webColor = Wrapper.toWebColor(color);
			data.getNode().setStyle("-fx-color: " + webColor);
		}
	}
}
