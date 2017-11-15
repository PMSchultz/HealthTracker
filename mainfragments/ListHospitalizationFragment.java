package edu.cnm.deepdive.healthtracker.mainfragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import edu.cnm.deepdive.healthtracker.MainActivity;
import edu.cnm.deepdive.healthtracker.R;
import edu.cnm.deepdive.healthtracker.entities.Hospitalization;
import edu.cnm.deepdive.healthtracker.entities.Patient;
import edu.cnm.deepdive.healthtracker.helpers.OrmHelper;
import edu.cnm.deepdive.healthtracker.helpers.OrmHelper.OrmInteraction;
import java.sql.SQLException;
import java.util.List;

public class ListHospitalizationFragment extends Fragment implements View.OnClickListener,
    AdapterView.OnItemClickListener{

  private Patient patient = null;
  private Hospitalization hospitalization = null;

  public ListHospitalizationFragment() {
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

  //TODO attach onClick Listeners to buttons
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
        toolbar.setTitle("Hospitalizations: " + patient.getName());
        OrmHelper helper = ((OrmInteraction) getActivity()).getHelper();
        Dao<Hospitalization, Integer> dao = helper.getHospitalizationDao();
        QueryBuilder<Hospitalization, Integer> builder = dao.queryBuilder();
        builder.where().eq("PATIENT_ID", patient.getId());
        builder.orderBy("ADMIT_DATE", false);
        List<Hospitalization> visits = dao.query(builder.prepare());
        chart.setAdapter(new ArrayAdapter<Hospitalization>(getActivity(), R.layout.list_item,
            visits));
        chart.setOnItemClickListener(this);
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




  public void onClick(View view) {

    switch (view.getId()){
      case R.id.add_record:
        ((MainActivity)getActivity()).loadFragment(new HospitalizationFragment(), patient.getId(),true);
        break;
      case R.id.edit_record:
        Bundle args = new Bundle();
        args.putInt(MainActivity.PATIENT_ID_KEY, patient.getId());
        args.putInt(HospitalizationFragment.HOSPITALIZATION_ID_KEY, hospitalization.getId());
        ((MainActivity)getActivity()).loadFragment(new HospitalizationFragment(), args,true);
        break;
      case R.id.delete_record:
        //TODO display Hospitalization record fragment populating fields with item selected and
        //popup with "are you sure you want to delete this record"
        break;
    }

  }

  @Override
  public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
    hospitalization = (Hospitalization) adapterView.getItemAtPosition(i);
    Button editButton = getActivity().findViewById(R.id.edit_record);
    editButton.setEnabled(true);
    Button deleteButton = getActivity().findViewById(R.id.delete_record);
    deleteButton.setEnabled(true);
  }
}


