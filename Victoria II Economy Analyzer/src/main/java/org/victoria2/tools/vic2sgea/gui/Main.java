package org.victoria2.tools.vic2sgea.gui;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.image.Image;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

import org.victoria2.tools.vic2sgea.entities.Country;
import org.victoria2.tools.vic2sgea.entities.History;
import org.victoria2.tools.vic2sgea.entities.Product;
import org.victoria2.tools.vic2sgea.main.Properties;
import org.victoria2.tools.vic2sgea.main.Report;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class Main extends Application {

	private static WindowController mainWindowController;

	private static Stage mainWindow;
	private static Stage unsavedChangesWindow;
	private static Stage productListWindow;
	private static Stage watcherWindow;
	// private static Stage historyWindow;

	public static boolean yesno;

	private double xOffset = 0;
	private double yOffset = 0;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {
		mainWindow = stage;
		Font.loadFont(getClass().getResourceAsStream("/NanumBarunGothic.ttf"), 14);

		FXMLLoader windowLoader = new FXMLLoader(getClass().getResource("/gui/Window.fxml"));
		Parent root = windowLoader.load();
		root.getStylesheets().add("/gui/style.css");

		root.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				xOffset = event.getSceneX();
				yOffset = event.getSceneY();
			}
		});
		root.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if (event.getButton().equals(MouseButton.PRIMARY)) {
					if (event.getClickCount() == 2) {
						mainWindowController.OnMaximize();
					}
				}
			}
		});
		root.setOnMouseDragged(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if (mainWindow.isMaximized()) {
					/*
					 * xOffset = event.getSceneX(); yOffset = event.getSceneY();
					 * setMainWindowPosition(xOffset, yOffset);
					 */
					mainWindowController.OnMaximize();
				}
				setMainWindowPosition(event.getScreenX() - xOffset, event.getScreenY() - yOffset);
			}
		});
		WindowController windowController = windowLoader.getController();
		mainWindowController = windowController;

		Properties props = new Properties();

		stage.setTitle("Victoria II 경제 분석기 " + props.getVersion() + " 한글판");
		stage.setScene(new Scene(root));
		// stage.setMinWidth(700);
		// stage.setMinHeight(500);
		stage.initStyle(StageStyle.UNDECORATED);
		stage.show();
		stage.getIcons().add(new Image("/flags/KOR.png"));

		UnsavedChangesController controller = new UnsavedChangesController();
		unsavedChangesWindow = new Stage();
		unsavedChangesWindow.setTitle("경고");
		unsavedChangesWindow.setScene(controller.getScene());
		unsavedChangesWindow.initStyle(StageStyle.UNDECORATED);
		unsavedChangesWindow.initModality(Modality.APPLICATION_MODAL);
		

		try {
			FXMLLoader productListLoader = new FXMLLoader(getClass().getResource("/gui/ProductList.fxml"));
			root = productListLoader.load();
			root.getStylesheets().add("/gui/style.css");

			windowController.setProductListController(productListLoader.getController());

			productListWindow = new Stage();
			productListWindow.setTitle("세계 시장");
			productListWindow.getIcons().add(new Image("/flags/KOR.png"));
			productListWindow.setScene(new Scene(root));

		} catch (IOException e) {
			e.printStackTrace();
		}

		initWatcherWindow();

	}

	public static WindowController getMainWindowController() {
		return mainWindowController;
	}

	public static void setMainWindowPosition(double x, double y) {
		mainWindow.setX(x);
		mainWindow.setY(y);
	}

	public static void minimizeMainWindow() {
		mainWindow.setIconified(true);
	}

	public static String maximizeMainWindow() {
		if (mainWindow.isMaximized()) {
			mainWindow.setMaximized(false);
			return "🗖";
		} else {
			mainWindow.setMaximized(true);
			return "🗗";
		}
	}

	public static void showCountry(Report report, Country country) {
		CountryController controller = new CountryController(report, country);

		Stage window = new Stage();
		window.setTitle(country.getOfficialName() + " 국가 정보");
		window.setScene(controller.getScene());

		window.show();
	}

	public static void showProduct(Report report, Product product) {
		ProductController controller = new ProductController(report, product);

		Stage productWindow = new Stage();
		productWindow.setTitle(product.getName() + " 상품 정보");
		productWindow.setScene(controller.getScene());

		productWindow.show();
	}

	private void initWatcherWindow() {
		FXMLLoader loader = new FXMLLoader(Main.class.getResource("/gui/Watchers.fxml"));
		Parent root;
		try {
			root = loader.load();
			root.getStylesheets().add("/gui/style.css");

			Stage window = new Stage();
			window.setTitle("Watchers");
			window.setScene(new Scene(root));

			watcherWindow = window;

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void showProductList() {
		productListWindow.show();
	}

	public static void hideProductList() {
		productListWindow.hide();
	}

	public static void showWatcherWindow() {
		watcherWindow.show();
	}

	public static void showInformationWindow() {
		try {
			FXMLLoader loader = new FXMLLoader(Main.class.getResource("/gui/Information.fxml"));
			Parent root = loader.load();
			root.getStylesheets().add("/gui/style.css");
			Stage stage = new Stage();
			stage.setTitle("프로그램 정보");
			stage.setScene(new Scene(root));
			stage.setResizable(false);
			stage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void showHistoryWindow(History history) {
		HistoryController controller = new HistoryController(history);
		Stage historyWindow = null;
		historyWindow = new Stage();
		historyWindow.setTitle("통계");
		historyWindow.setScene(controller.getScene());
		historyWindow.show();
	}
	
	public static void showCountryHistoryWindow(History history, String countryName)
	{
		CountryHistoryController controller = new CountryHistoryController(history, countryName);
		Stage countryhistoryWindow = null;
		countryhistoryWindow = new Stage();
		countryhistoryWindow.setTitle(countryName + " 통계");
		countryhistoryWindow.setScene(controller.getScene());
		countryhistoryWindow.show();
	}

	public static void showUnsavedChangesWindow() {
		yesno = false;
		unsavedChangesWindow.showAndWait();
	}

	static class UnsavedChangesController extends BaseController {

		protected final Scene scene;
		private GridPane grid;

		public UnsavedChangesController() {
			
			grid = new GridPane();
			grid.setHgap(5);
			grid.setVgap(5);
			grid.setPadding(new Insets(10, 10, 10, 10));
			grid.setAlignment(Pos.BASELINE_CENTER);
			scene = new Scene(grid);
			scene.getStylesheets().add("/gui/style.css");

			Label label = new Label("저장되지 않은 정보가 있습니다.\n계속하시겠습니까?");
			
			grid.add(label, 0, 0);
			GridPane.setHalignment(label, HPos.CENTER);
			Button yes = new Button("예");
			Button no = new Button("아니오");
			yes.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					yesno = true;
					unsavedChangesWindow.hide();
				}

			});
			no.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					yesno = false;
					unsavedChangesWindow.hide();
				}

			});
			grid.add(yes, 0, 1);
			grid.add(no, 0, 1);
			GridPane.setHalignment(yes,  HPos.LEFT);
			GridPane.setHalignment(no,  HPos.RIGHT);

		}

		public Scene getScene() {
			return scene;
		}

	}
}
