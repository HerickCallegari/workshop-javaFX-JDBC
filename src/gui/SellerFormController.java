package gui;

import java.net.URL;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import db.DbException;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.util.Callback;
import model.entities.Department;
import model.entities.Seller;
import model.exceptions.ValidationException;
import model.services.DepartmentService;
import model.services.SellerService;

public class SellerFormController implements Initializable {

	private Seller entity;

	private SellerService service;

	private DepartmentService depService;

	private List<DataChangeListener> dataChangeListeners = new ArrayList<>();

	@FXML
	private TextField txtId;

	@FXML
	private TextField txtName;

	@FXML
	private TextField txtEmail;

	@FXML
	private DatePicker dpBirthDate;

	@FXML
	private TextField txtSalary;

	@FXML
	private ComboBox<Department> comboBoxDepartments;

	private ObservableList<Department> obsList;

	@FXML
	private Label lbErrorName;

	@FXML
	private Label lbErrorSalary;

	@FXML
	private Label lbErrorBirthDate;

	@FXML
	private Label lbErrorEmail;

	@FXML
	private Label lbErrorDepartment;

	@FXML
	private Button btSave;

	@FXML
	private Button btCancel;

	@FXML
	public void onBtSaveAction(ActionEvent event) {
		if ( entity == null ) {
			throw new IllegalStateException("Seller was null");
		}
		if ( service == null ) {
			throw new IllegalStateException("service was null");
		}
		try {
		entity = getFormData();
		service.saveOrUpdate(entity);
		notifyDataChangeListener();
		Utils.currentStage(event).close();
		} catch ( ValidationException e) {
			setErrorMenssage(e.getErrors());
		}catch (DbException e) {
			Alerts.showAlert("Error saving Object", "DBException", e.getMessage(), AlertType.ERROR);
		}
	}
	private void notifyDataChangeListener() {
		for(DataChangeListener listener: dataChangeListeners) {
			listener.onDataChanged();
		}
}

	@FXML
	public void onBtCancelAction(ActionEvent event) {
		Utils.currentStage(event).close();
	}

	public void loadAssociatedObjects() {
		obsList = FXCollections.observableArrayList(depService.findAll());
		comboBoxDepartments.setItems(obsList);
	}

	public void setSeller(Seller obj) {
		entity = obj;
	}

	public Seller getSeller() {
		return entity;
	}

	public void setServices(SellerService service, DepartmentService depService) {
		this.service = service;
		this.depService = depService;
	}

	public void updateFormData() {
		if (entity == null) {
			throw new IllegalStateException("Seller was null");
		}
		if (entity.getId() == null) {
			txtId.setText("");
		}else {
		txtId.setText(String.valueOf(entity.getId()));
		}
		txtName.setText(entity.getName());
		txtEmail.setText(entity.getEmail());
		txtSalary.setText(String.valueOf(entity.getBaseSalary()));
		if (entity.getBirthDate() != null) {
			dpBirthDate.setValue(LocalDate.ofInstant(entity.getBirthDate().toInstant(), ZoneId.systemDefault()));
		}
		if (entity.getDepartment() == null) {
			comboBoxDepartments.getSelectionModel().selectFirst();
		}else {
		comboBoxDepartments.setValue(entity.getDepartment());
		}
	}
	
	private Seller getFormData() {
		Seller obj = new Seller();
		ValidationException exception = new ValidationException("Validation Error");
		
		obj.setId(Utils.tryParseToInt(txtId.getText()));
		
		if ( txtName.getText() == null || txtName.getText().trim().equals("")) {
			exception.addError("Name", "Name can't be empity");
		}
		obj.setName(txtName.getText());
		
		if ( txtEmail.getText() == null || txtEmail.getText().trim().equals("")) {
			exception.addError("Email", "Email can't be empity");
		}
		obj.setEmail(txtEmail.getText());
		
		if ( txtSalary.getText() == null || txtSalary.getText().trim().equals("")) {
			exception.addError("Salary", "Salary can't be empity");
		}else {
		obj.setBaseSalary(Utils.tryParseToDouble(txtSalary.getText()));
		}
		if (dpBirthDate.getValue() == null) {
			exception.addError("Date", "Date can't be empity");
		}else{
		Instant instant = Instant.from(dpBirthDate.getValue().atStartOfDay(ZoneId.systemDefault()));
		obj.setBirthDate(Date.from(instant));
		}
		
		if (comboBoxDepartments.getValue() == null) {
			exception.addError("Department", "Department can't be empity");
		} else {
		obj.setDepartment(comboBoxDepartments.getValue());
		}
		
		if (exception.getErrors().size() > 0 ) {
			throw exception;
		}
		
		return obj;
	}

	public void subscribeDataChangeListener(SellerListController sellerListController) {
		dataChangeListeners.add(sellerListController);
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		initializaNode();
	}

	private void initializaNode() {
		Utils.formatDatePicker(dpBirthDate, "dd/MM/yyyy");
		initializeComboBoxDepartment();
	}

	private void initializeComboBoxDepartment() {
		Callback<ListView<Department>, ListCell<Department>> factory = lv -> new ListCell<Department>() {
			@Override
			protected void updateItem(Department item, boolean empty) {
				super.updateItem(item, empty);
				setText(empty ? "" : item.getName());
			}
		};
		comboBoxDepartments.setCellFactory(factory);
		comboBoxDepartments.setButtonCell(factory.call(null));
	}
	
	private void setErrorMenssage(Map<String, String> errors) {
		Set<String> fields = errors.keySet();
		
		lbErrorName.setText( fields.contains("Name") ? errors.get("Name") : "" );
		
		lbErrorEmail.setText(fields.contains("Email")? errors.get("Email") : "");
		
		lbErrorSalary.setText(fields.contains("Salary") ? errors.get("Salary") : "");
		
		lbErrorBirthDate.setText(fields.contains("Date") ? errors.get("Date") : "");
		
		lbErrorDepartment.setText(fields.contains("Department") ? errors.get("Department") : "");
	}
}
