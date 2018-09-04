package org.victoria2.tools.vic2sgea.gui;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

public class InformationController extends BaseController implements Initializable {

	@FXML
	Label labelLicense0;
	@FXML
	Label labelLicense1;
	@FXML
	Label labelLicense2;
	@FXML
	Label labelLicense3;
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		
		labelLicense0.setText("Copyright (c) 2013, Nashetovich");
		labelLicense1.setText("Copyright (c) 2017, Anton Krylov (github.com/aekrylov)");
		labelLicense2.setText("Copyright (c) 2018, 퍼플피닉스 (Dcinside)");
		labelLicense3.setText("All rights reserved."); 

	}

}
