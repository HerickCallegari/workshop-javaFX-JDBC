package gui;

import java.net.URL;
import java.util.ResourceBundle;

import db.DbException;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.entities.Department;
import model.services.DepartmentService;

public class DepartmentFormController implements Initializable {

	private Department entity;

	private DepartmentService service;

	@FXML
	private TextField txtId;

	@FXML
	private TextField txtName;

	@FXML
	private Label lbError;

	@FXML
	private Button btSave;

	@FXML
	private Button btCancel;

	@FXML
	public void onBtSaveAction(ActionEvent event) {
		if (entity == null) {
			throw new IllegalStateException("entity was null");
		}
		if (service == null) {
			throw new IllegalStateException("service was null");
		}
		try {
		entity = getFormDepartment();
		service.saveOrUpdate(entity);
		Utils.currentStage(event).close();
		}catch (DbException e) {
			Alerts.showAlert("Error saving Object", "DBException", e.getMessage(), AlertType.ERROR);
		}
	}

	private Department getFormDepartment() {
		Department dep = new Department();
		dep.setId(Utils.tryParseToInt(txtId.getText()));
		dep.setName(txtName.getText());
		return dep;
	}

	@FXML
	public void onBtCancelAction(ActionEvent event) {
		System.out.println("cancel");
		Utils.currentStage(event).close();
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes();
	}

	private void initializeNodes() {
		Constraints.setTextFieldInteger(txtId);
	}

	public void setDepartment(Department entity) {
		this.entity = entity;
	}

	public void setDepartmentService(DepartmentService service) {
		this.service = service;
	}

	public void updateFormData() {
		if (entity == null) {
			throw new IllegalStateException("entity ad null");
		}
		txtId.setText(String.valueOf(entity.getId()));
		txtName.setText(entity.getName());
	}

}
