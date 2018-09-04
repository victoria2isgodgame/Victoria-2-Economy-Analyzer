package org.victoria2.tools.vic2sgea.gui;

import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.util.StringConverter;
import org.victoria2.tools.vic2sgea.entities.Country;
import org.victoria2.tools.vic2sgea.entities.History;
import org.victoria2.tools.vic2sgea.main.PathKeeper;
import org.victoria2.tools.vic2sgea.main.Report;
import org.victoria2.tools.vic2sgea.main.TableRowDoubleClickFactory;
import org.victoria2.tools.vic2sgea.main.Wrapper;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;

/**
 * @author nashet
 */
public class WindowController extends BaseController implements Initializable {
	
	private boolean issaved = true;

	private double xOffset = 0;
	private double yOffset = 0;

	@FXML
	MenuBar headerMenu;
	@FXML
	Button btnMinimize;
	@FXML
	Button btnMaximize;
	@FXML
	Button btnLoad;
	@FXML
	Button btnGoods;
	@FXML
	public Label lblStartDate;
	@FXML
	public Label lblCurrentDate;
	@FXML
	public Label lblPlayer;
	@FXML
	public Label lblPopCount;

	@FXML
	GridPane treegrid;
	@FXML
	TreeView<String> mainTree;

	@FXML
	TableView<Country> mainTable;

	@FXML
	FilePrompt fpSaveGame;
	@FXML
	FilePrompt fpGamePath;
	@FXML
	FilePrompt fpModPath;
	@FXML
	public Pane progressWrap;
	@FXML
	ProgressIndicator piLoad;

	private static final ObservableList<Country> countryTableContent = FXCollections.observableArrayList();

	private ProductListController productListController;

	@FXML
	TableColumn<Country, Integer> colGDPPlace;
	@FXML
	public TableColumn<Country, ImageView> colImage;
	@FXML
	public TableColumn<Country, String> colCountry;
	@FXML
	TableColumn<Country, Float> colGdp;
	@FXML
	TableColumn<Country, Float> colGDPPer;
	@FXML
	TableColumn<Country, Float> colGDPPart;
	@FXML
	public TableColumn<Country, String> colGovernment;
	@FXML
	public TableColumn<Country, Long> colPopulation;
	@FXML
	TableColumn<Country, Float> colConsumption;
	@FXML
	TableColumn<Country, Float> colActualSupply;
	@FXML
	TableColumn<Country, Long> colGoldIncome;
	@FXML
	TableColumn<Country, Long> colWorkForceRgo;
	@FXML
	TableColumn<Country, Long> colWorkForceFactory;
	@FXML
	TableColumn<Country, Long> colEmployment;
	@FXML
	TableColumn<Country, Float> colImport;
	@FXML
	TableColumn<Country, Float> colExport;
	@FXML
	TableColumn<Country, Float> colUnemploymentRate;
	@FXML
	TableColumn<Country, Float> colUnemploymentRateFactory;

	private void fillMainTable() {
		countryTableContent.clear();
		countryTableContent.addAll(report.getCountryList());
		mainTable.setItems(countryTableContent);
	}

	private WindowController self;

	private Report report;

	public static History history;
	
	Thread threadSaveLoad;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {

		treegrid.getStyleClass().add("treegrid");

		mainTree.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if (event.getButton().equals(MouseButton.PRIMARY)) {
					if (event.getClickCount() == 2) {
						TreeItem<String> item = mainTree.getSelectionModel().getSelectedItem();
						if (item.getChildren().size() == 0) {
							report = history.getReport(item.getValue());
							LoadTable();
							mainTable.getSortOrder().add(colGDPPlace);
							productListController.productsTable.getSortOrder().add(productListController.colName);
						} else {
							Main.showHistoryWindow(history);
						}
					}
				}
			}
		});

		// add row click handler
		mainTable.setRowFactory(new TableRowDoubleClickFactory<>(country -> Main.showCountry(report, country)));

		colImage.setCellValueFactory(features -> {
			String tag = features.getValue().getTag();
			String flag = features.getValue().getFlag();
			URL url = getClass().getResource("/flags/" + tag + flag + ".png");
			if (url == null)
				return null;

			Image image = new Image(url.toString());
			ImageView iv = new ImageView(image);
			iv.setPreserveRatio(true);
			iv.setFitHeight(20);
			iv.getStyleClass().add("flag");
			return new SimpleObjectProperty<>(iv);
		});

		setFactory(colCountry, Country::getOfficialName);
		setFactory(colGovernment, Country::getGovernment);
		setFactory(colPopulation, Country::getPopulation);
		setFactory(colActualSupply, Country::getSold);
		setFactory(colGdp, Country::getGdp);
		setFactory(colConsumption, Country::getBought);
		setFactory(colGDPPer, Country::getGdpPerCapita);
		setFactory(colGDPPlace, Country::getGDPPlace);
		setFactory(colGDPPart, Country::getGDPPart);
		setFactory(colGoldIncome, Country::getGoldIncome);

		setFactory(colWorkForceRgo, Country::getWorkforceRgo);
		setFactory(colWorkForceFactory, Country::getWorkforceFactory);
		setFactory(colEmployment, Country::getEmployment);

		setFactory(colImport, Country::getImported);
		setFactory(colExport, Country::getExported);

		setFactory(colUnemploymentRate, Country::getUnemploymentRateRgo);
		setFactory(colUnemploymentRateFactory, Country::getUnemploymentRateFactory);

		setCellFactory(colPopulation, new KmgConverter<>());
		setCellFactory(colActualSupply, new KmgConverter<>());
		setCellFactory(colGdp, new KmgConverter<>());
		setCellFactory(colGDPPart, new PercentageConverter());
		setCellFactory(colGDPPer, new NiceFloatConverter());
		setCellFactory(colWorkForceRgo, new KmgConverter<>());
		setCellFactory(colWorkForceFactory, new KmgConverter<>());
		setCellFactory(colEmployment, new KmgConverter<>());
		setCellFactory(colUnemploymentRate, new PercentageConverter());
		setCellFactory(colUnemploymentRateFactory, new PercentageConverter());

		colConsumption.setVisible(false);
		colActualSupply.setVisible(false);
		colGoldIncome.setVisible(false);

		/*
		 * try { Config config = new Config(); } catch (IOException e) {
		 * e.printStackTrace(); errorAlert(e, "Couldn't load config"); }
		 */
		PathKeeper.checkPaths();
		fpGamePath.setPath(PathKeeper.LOCALISATION_PATH);
		fpModPath.setPath(PathKeeper.MOD_PATH);

		lblPlayer.setOnMouseClicked(e -> {
			if (report != null) {
				Main.showCountry(report, report.getPlayerCountry());
			}

		});

		headerMenu.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if (event.getButton().equals(MouseButton.PRIMARY)) {
					if (event.getClickCount() == 2) {
						OnMaximize();
					}
				}
			}
		});

		headerMenu.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				xOffset = event.getSceneX();
				yOffset = event.getSceneY();
			}
		});

		headerMenu.setOnMouseDragged(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				Main.setMainWindowPosition(event.getScreenX() - xOffset, event.getScreenY() - yOffset);
			}
		});

		self = this;
	}

	public void onMenuNew() {
		if(issaved)
		{
			history = null;
			report = null;
			loadMainTree();
			mainTable.getItems().clear();
		}
		else
		{
			Main.showUnsavedChangesWindow();
			if(Main.yesno)
			{
				history = null;
				report = null;
				loadMainTree();
				mainTable.getItems().clear();
				issaved = true;
			}
			else
				return;
		}
	}

	public void onMenuLoad() {
		FileChooser chooser = new FileChooser();
		chooser.setInitialDirectory((new File(PathKeeper.SAVE_PATH)).getParentFile());
		chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("빅토리아 2 세이브 파일", "*.v2"));
		chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("경제분석기 역사 파일", "*.v2h"));
		List<File> files = chooser.showOpenMultipleDialog(null);
		if(files == null)
			return;

		File file = files.get(0);
		String path = file.getPath();
		PathKeeper.SAVE_PATH = path;
		PathKeeper.save();
		String filetype = file.getName().substring(file.getName().lastIndexOf(".") + 1, file.getName().length());
		System.out.println(filetype);
		if (filetype.matches("v2")) {
			LoadSave(files);

		} else if (filetype.matches("v2h")) {
			LoadHist(file);
		}

	}

	public void onMenuSave() {
		FileChooser chooser = new FileChooser();
		chooser.setInitialDirectory((new File(PathKeeper.SAVE_PATH)).getParentFile());
		chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("경제분석기 역사 파일", "*.v2h"));

		File file = chooser.showSaveDialog(null);
		if(file == null)
			return;
		String path = file.getPath();
		PathKeeper.SAVE_PATH = path;
		PathKeeper.save();
		try {
			Gson gson = new Gson();
			String json = gson.toJson(history);
			FileOutputStream fs = new FileOutputStream(file);
			OutputStreamWriter sw = new OutputStreamWriter(fs, "MS949");
			BufferedWriter writer = new BufferedWriter(sw);
			writer.write(json);
			writer.close();
			sw.close();
			fs.close();
		} catch (IOException e) {
		}

	}

	public void LoadSave(List<File> files) {
		threadSaveLoad = null;
		setInterfaceEnabled(false);

		Task<Integer> task = new Task<Integer>() {
			@Override
			protected Integer call() throws Exception {

				System.out.println("Nash: calc thread started...");
				float startTime = System.nanoTime();
				// float startTime=0;

				try {
					for (File file : files) {
						report = new Report(file.getPath(), PathKeeper.LOCALISATION_PATH, PathKeeper.MOD_PATH);

						if (history == null) {
							history = new History("");
						}
						if (history.getReportAll().get(report.getCurrentDate()) != null) {
							history.getReportAll().remove(report.getCurrentDate());
						}
						history.addReport(report.getCurrentDate().toString(), report);
					}

				} catch (Exception e) {
					e.printStackTrace();
					errorAlert(e, "세이브 파일 불러오는 중 예외발생");
				} finally {
					Platform.runLater(() -> setInterfaceEnabled(true));
					Platform.runLater(() -> LoadTable());
					Platform.runLater(() -> loadMainTree());
					Platform.runLater(() -> mainTable.getSortOrder().add(colGDPPlace));
					Platform.runLater(() -> productListController.productsTable.getSortOrder()
							.add(productListController.colName));

				}
				return 0;
			}
		};
		threadSaveLoad = new Thread(task);
		threadSaveLoad.start();
		issaved = false;
	}

	public void LoadSave(File file) {
		List<File> files = new ArrayList<>();
		files.add(file);
		LoadSave(files);
	}

	public void LoadHist(File file) {
		threadSaveLoad = null;
		setInterfaceEnabled(false);
		
		history = null;
		Task<Integer> task = new Task<Integer>() {
			@Override
			protected Integer call() throws Exception {

				System.out.println("Nash: calc thread started...");
				float startTime = System.nanoTime();
				// float startTime=0;

				try {
					Gson gson = new Gson();
					FileInputStream fs = new FileInputStream(file);
					InputStreamReader sr = new InputStreamReader(fs, "MS949");
					BufferedReader reader = new BufferedReader(sr);
					JsonReader jr = new JsonReader(reader);
					history = gson.fromJson(jr, History.class);

					for (Report rep : history.getReportAll().values()) {
						report = rep;
					}

					jr.close();
					reader.close();
					sr.close();
					fs.close();

				} catch (Exception e) {
					e.printStackTrace();
					errorAlert(e, "세이브 파일 불러오는 중 예외발생");
				} finally {
					Platform.runLater(() -> setInterfaceEnabled(true));
					Platform.runLater(() -> LoadTable());
					Platform.runLater(() -> loadMainTree());
					Platform.runLater(() -> mainTable.getSortOrder().add(colGDPPlace));
					Platform.runLater(() -> productListController.productsTable.getSortOrder()
							.add(productListController.colName));
				}
				return 0;
			}
		};
		threadSaveLoad = new Thread(task);
		threadSaveLoad.start();
		
	}

	void loadMainTree() {
		if (history != null) {
			TreeItem<String> histTree = new TreeItem<>("");
			mainTree.setRoot(histTree);
			for (Report report : history.getReportAll().values()) {
				TreeItem<String> dateTree = new TreeItem<String>(report.getCurrentDate());
				histTree.getChildren().add(dateTree);
			}
			mainTree.getRoot().getChildren().sort(new Comparator<TreeItem<String>>() {
				@Override
				public int compare(TreeItem<String> o1, TreeItem<String> o2) {
					double v1 = Integer.parseInt(o1.getValue().replaceAll("\\.", ""));
					double v2 = Integer.parseInt(o2.getValue().replaceAll("\\.", ""));
					if (v1 > v2)
						return 1;
					else if (v1 < v2)
						return -1;
					else
						return 0;
				}
			});

			ObservableList<TreeItem<String>> reportlist = mainTree.getRoot().getChildren();
			String histname = history.getReport(reportlist.get(reportlist.size() - 1).getValue()).getPlayerCountry()
					.getOfficialName();
			history.setName(histname);
			mainTree.getRoot().setValue(histname);
		}
		else
			mainTree.setRoot(null);
	}

	void LoadTable() {
		fillMainTable();

		productListController.setReport(report);
		productListController.fillTable(report.getProductList());
		setLabels();

	}

	public final void onGoods(ActionEvent event) {
		Main.showProductList();
	}

	private void setInterfaceEnabled(boolean isEnabled) {
		progressWrap.setVisible(!isEnabled);
		progressWrap.toFront();
	}

	public void onLoad() {

		// Main.hideProductList();
		setInterfaceEnabled(false);

		Task<Integer> task = new Task<Integer>() {
			@Override
			protected Integer call() throws Exception {

				System.out.println("Nash: calc thread started...");
				float startTime = System.nanoTime();
				// float startTime=0;

				try {
					String savePath = fpSaveGame.getPath();
					String modPath = fpModPath.getPath();
					String gamePath = fpGamePath.getPath();

					PathKeeper.SAVE_PATH = savePath;
					PathKeeper.MOD_PATH = modPath;
					PathKeeper.LOCALISATION_PATH = gamePath;
					PathKeeper.save();

					report = null;
					report = new Report(savePath, gamePath, modPath);

					fillMainTable();
					productListController.setReport(report);
					productListController.fillTable(report.getProductList());

					mainTable.getSortOrder().add(colGDPPlace);
					productListController.productsTable.getSortOrder().add(productListController.colName);

					float res = ((float) System.nanoTime() - startTime) / 1000000000;
					System.out.println("Nash: total time is " + res + " seconds");

					Platform.runLater(() -> setLabels());

				} catch (Exception e) {
					e.printStackTrace();
					errorAlert(e, "세이브 파일 불러오는 중 예외발생");
				} finally {
					Platform.runLater(() -> setInterfaceEnabled(true));

				}
				return 0;
			}
		};
		Thread th = new Thread(task);
		th.start();

	}

	public void OnMinimize() {
		Main.minimizeMainWindow();

	}

	public void OnMaximize() {

		btnMaximize.setText(Main.maximizeMainWindow());
	}

	private void setLabels() {
		lblCurrentDate.setText(report.getCurrentDate());
		lblPlayer.setText(report.getPlayerCountry().getOfficialName());
		lblStartDate.setText(report.getStartDate());
		lblPopCount.setText("총 " + report.popCount + " 팝");
	}

	public void setProductListController(ProductListController productListController) {
		this.productListController = productListController;
	}

	public void createNewHistory() {
	}

	public void onSetActiveWatcher() {
		// prompt file name and dir to scan
		Main.showWatcherWindow();
	}

	public void onInformation() {
		Main.showInformationWindow();

	}
}

class KmgConverter<T extends Number> extends StringConverter<T> {

	@Override
	public String toString(T object) {
		return Wrapper.toKMG(object);
	}

	// don't need this
	@Override
	public T fromString(String string) {
		return null;
	}
}

class PercentageConverter extends StringConverter<Float> {

	@Override
	public String toString(Float object) {
		return Wrapper.toPercentage(object);
	}

	// don't need this
	@Override
	public Float fromString(String string) {
		return null;
	}
}

class NiceFloatConverter extends StringConverter<Float> {

	@Override
	public String toString(Float object) {
		return String.format("%3.6f", object);
	}

	// don't need this
	@Override
	public Float fromString(String string) {
		return null;
	}
}

class HighFloatConverter extends StringConverter<Float> {

	@Override
	public String toString(Float object) {
		return String.format("%.2f", object);
	}

	// don't need this
	@Override
	public Float fromString(String string) {
		return null;
	}
}

