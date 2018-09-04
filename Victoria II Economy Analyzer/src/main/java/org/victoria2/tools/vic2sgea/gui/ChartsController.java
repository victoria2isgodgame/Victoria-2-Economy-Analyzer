package org.victoria2.tools.vic2sgea.gui;

import javafx.collections.FXCollections;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import org.victoria2.tools.vic2sgea.main.Report;
import org.victoria2.tools.vic2sgea.main.Wrapper;

import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * By Anton Krylov (anthony.kryloff@gmail.com)
 * Date: 2/2/17 12:05 AM
 * <p>
 * Base controller for chart windows
 */
public class ChartsController extends BaseController {
    protected final Scene scene;
    protected final GridPane grid;
    protected final Report report;

    private int chartCount = 0;

    public ChartsController(Report report, String titlename) {
        this.report = report;
        this.grid = new GridPane();

        //grid.setAlignment(Pos.CENTER);
        grid.setAlignment(Pos.TOP_CENTER);
        grid.setHgap(5);
        grid.setVgap(5);
        grid.setPadding(new Insets(5, 5, 5, 5));        
        grid.setStyle("-fx-background-color: #400000");
        
        
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(grid);

        scene = new Scene(scrollPane);
        scene.getStylesheets().add("/gui/charts.css");
        
        Label title = new Label(titlename);
        title.getStyleClass().add("title");
        grid.add(title, 1, 0);
        
        GridPane.setHalignment(title, HPos.CENTER);
    }

    /**
     * Adds chart to grid
     *
     * @param pieChartData chart data
     * @param i            column index
     * @param j            row index
     * @param title        chart title
     * @param onEnter      function that returns caption when mouse enters an item
     * @param onClick      consumer called when an item is clicked
     * @return the created chart
     */
    protected PieChart addChart(List<PieChart.Data> pieChartData, int i, int j, String title,
                                Function<PieChart.Data, String> onEnter, Consumer<PieChart.Data> onClick) {
        pieChartData.sort(Comparator.comparing(PieChart.Data::getPieValue).reversed());
        final PieChart chart = new PieChart(FXCollections.observableList(pieChartData));
        chart.setMaxSize(400, 400);
        chart.setPrefSize(400, 400);        
        chart.setStartAngle(90);
        chart.setLegendVisible(false);
        //chart.setLabelsVisible(true);
        //chart.setTitle(title);
        chart.setTitleSide(Side.TOP);

        final Label titlelabel = new Label(title);
        titlelabel.getStyleClass().add("chart-title");
        GridPane.setValignment(titlelabel, VPos.TOP);
        GridPane.setHalignment(titlelabel, HPos.CENTER);
        
        final Label caption = new Label("");
        caption.getStyleClass().add("chart-caption");
        GridPane.setValignment(caption, VPos.BOTTOM);
        
        GridPane subPane = new GridPane();
        subPane.setAlignment(Pos.BASELINE_CENTER);
        subPane.add(titlelabel, 0, 0);
        subPane.add(chart, 0, 0);
        subPane.add(caption, 0, 0);
        subPane.setPadding(new Insets(0, 0, 0, 0));
        subPane.getStyleClass().add("chartgrid");
        
        grid.add(subPane, i, j+2);
        

        Double totalValue = chart.getData().stream()
                .map(PieChart.Data::getPieValue)
                .reduce(0., (d1, d2) -> d1 + d2);

        for (final PieChart.Data data : chart.getData()) {
        	if (data.getPieValue() / totalValue < .001) {
                data.getNode().setVisible(false);
                continue;
            }
            data.getNode().addEventHandler(MouseEvent.MOUSE_ENTERED,
                    e -> caption.setText(onEnter.apply(data)));
            data.getNode().addEventHandler(MouseEvent.MOUSE_CLICKED,
                    e -> onClick.accept(data));
        }

        return chart;
    }

    /*protected PieChart addChart(List<PieChart.Data> pieChartData, String title,
                                Function<PieChart.Data, String> onEnter, Consumer<PieChart.Data> onClick) {
        int row = (chartCount / 2);
        int column = chartCount % 2;

        chartCount++;
        return addChart(pieChartData, column, row, title, onEnter, onClick);

    }*/

    protected PieChart addChart(List<ChartSlice> slices, String title,
                                Function<PieChart.Data, String> onEnter, Consumer<PieChart.Data> onClick) {
        int row = chartCount / 3;
        int column = chartCount % 3;

        chartCount++;

        List<PieChart.Data> pieChartData = slices.stream()
                .map(chartSlice -> chartSlice.data)
                .collect(Collectors.toList());

        PieChart chart = addChart(pieChartData, column, row, title, onEnter, onClick);
        //todo ugly colors
        //slices.forEach(ChartSlice::setColor);

        return chart;

    }

    public Scene getScene() {
        return scene;
    }

    static class ChartSlice {
        private Color color;
        private PieChart.Data data;
        

        public ChartSlice(String name, double value, Color color) {
            this(name, value);
            this.color = color;
        }

        public ChartSlice(String name, double value) {
            data = new PieChart.Data(name, value);
        }

        void setColor() {
            if (color == null)
                return;
            String webColor = Wrapper.toWebColor(color);
            data.getNode().setStyle("-fx-pie-color: " + webColor);
        }
    }
}
