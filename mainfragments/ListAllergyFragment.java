package edu.cnm.deepdive.healthtracker.mainfragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import edu.cnm.deepdive.healthtracker.MainActivity;
import edu.cnm.deepdive.healthtracker.R;
import edu.cnm.deepdive.healthtracker.entities.Allergy;
import edu.cnm.deepdive.healthtracker.entities.OfficeVisit;
import edu.cnm.deepdive.healthtracker.entities.Patient;
import edu.cnm.deepdive.healthtracker.helpers.OrmHelper;
import edu.cnm.deepdive.healthtracker.helpers.OrmHelper.OrmInteraction;
import java.sql.SQLException;
import java.util.List;

public class ListAllergyFragment extends Fragment implements View.OnClickListener {

  private Patient patient = null;

  public ListAllergyFragment() {
    // Required empty public constructor
  }


  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Bundle args = getArguments();
    int patientID;
    if (args != null && (patientID = args.getInt(MainActivity.PATIENT_ID_KEY, 0)) != 0) {
      try {
        patient = ((OrmInteraction) getActivity()).getHelper().getPatientDao()
            .queryForId(patientID);
      } catch (SQLException e) {
        throw new RuntimeException(e);
      }

    }
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View inflate = inflater.inflate(R.layout.fragment_list, container, false);

    setupList(inflate);
    setupButtons(inflate);

    return inflate;
  }

  private void setupList(View inflate) {
    if (patient != null) {

      try {
        ListView chart = inflate
            .findViewById(R.id.content_list);//creating a reference to the chart contents
        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle("Allergies: " + patient.getName());
        OrmHelper helper = ((OrmInteraction) getActivity()).getHelper();
        Dao<Allergy, Integer> dao = helper.getAllergyDao();
        QueryBuilder<Allergy, Integer> builder = dao.queryBuilder();
        builder.where().eq("PATIENT_ID", patient.getId());
        //builder.orderBy("DATE", false);
        List<Allergy> visits = dao.query(builder.prepare());
        chart.setAdapter(new ArrayAdapter<Allergy>(getActivity(), R.layout.list_item,
            visits));
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }
  }

//set OnClickListener to buttons
  private void setupButtons(View rootView) {
    Button addButton = rootView.findViewById(R.id.add_record);
    addButton.setOnClickListener(this);
    Button editButton = rootView.findViewById(R.id.edit_record);
    editButton.setOnClickListener(this);
    Button deleteButton = rootView.findViewById(R.id.delete_record);
    deleteButton.setOnClickListener(this);
  }

  @Override
  public void onClick(View view) {

    switch (view.getId()){
      case R.id.add_record:
        ((MainActivity)getActivity()).loadFragment(new AllergyFragment(), patient.getId(),true);
        break;
      case R.id.edit_record:
        //TODO display Allergy record fragment populating fields with item selected
        break;
      case R.id.delete_record:
        //TODO display Allergy record fragment populating fields with item selected and
        //popup with "are you sure you want to delete this record"
        break;
    }

  }
}
