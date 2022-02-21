package application;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javafx.animation.FadeTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.Duration;

public class MemoryController extends MainController implements Initializable{

    @FXML
    private TableView<PageTable> memory_table;

    @FXML
    private TableColumn<PageTable, String> name;
    
    @FXML
    private TableColumn<PageTable, Integer> id;

    @FXML
    private TableColumn<PageTable, Integer> frame;

    @FXML
    private TableColumn<PageTable, Boolean> valid;

    @FXML
    private TableColumn<PageTable, Boolean> dirty;
	
    @FXML
    private Button back_button;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		ArrayList<PageTable> data = new ArrayList<>();
		ObservableList<PageTable> dataList = FXCollections.observableArrayList();
		for(int i=0;i<memory_size;i++) {
			data.add(new PageTable(memory[i].getPid(),memory[i].getProcess_name(),memory[i].isValid(),memory[i].getFrame_num(),memory[i].isDirty()));
			dataList.add(data.get(i));
		}
		name.setCellValueFactory(new PropertyValueFactory<PageTable,String>("process_name"));
		id.setCellValueFactory(new PropertyValueFactory<PageTable,Integer>("pid"));
		frame.setCellValueFactory(new PropertyValueFactory<PageTable,Integer>("frame_num"));
		valid.setCellValueFactory(new PropertyValueFactory<PageTable,Boolean>("valid"));
		dirty.setCellValueFactory(new PropertyValueFactory<PageTable,Boolean>("dirty"));
		memory_table.setItems(dataList);
	}

    @FXML
    void back_to_main(ActionEvent event) throws IOException {
        Stage stage = (Stage) back_button.getScene().getWindow();
		FadeTransition ft = new FadeTransition(Duration.millis(500));
		ft.setFromValue(0.0);
		ft.setToValue(1.0);
		ft.play();
        stage.close();
    }
}

	