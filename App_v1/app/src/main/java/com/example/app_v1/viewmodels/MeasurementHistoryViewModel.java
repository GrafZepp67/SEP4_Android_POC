package com.example.app_v1.viewmodels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.app_v1.models.Co2;
import com.example.app_v1.models.Humidity;
import com.example.app_v1.models.Temperature;
import com.example.app_v1.repositories.MeasurementRepository;

import java.util.ArrayList;

public class MeasurementHistoryViewModel extends ViewModel
{
    private MeasurementRepository repo;

    private MutableLiveData<Integer> selectedTabIndex = new MutableLiveData<>();
    private MutableLiveData<Integer> selectedGreenhouseId = new MutableLiveData<>();
    private ArrayList<Temperature> temperaturesInDateRange = new ArrayList<>();
    private MutableLiveData<ArrayList<Temperature>> temperaturesInRangeMld = new MutableLiveData<>();
    private ArrayList<Humidity> humidityInDateRange = new ArrayList<>();
    private ArrayList<Co2> co2InDateRange = new ArrayList<>();

    public void initViewModel()
    {
        repo = MeasurementRepository.getInstance();
        selectedTabIndex.setValue(0);
        selectedGreenhouseId.setValue(0);
    }

    public Boolean validateSearchParameters(String dateRangeString, String timeFrom, String timeTo)
    {
        boolean isValid;

        isValid = !dateRangeString.equals("Select") && !timeFrom.equals("Select") && !timeTo.equals("Select");

        return isValid;
    }

    public void setSelectedTabIndex(Integer index)
    {
        selectedTabIndex.postValue(index);
    }

    public MutableLiveData<Integer> getSelectedTabIndex()
    {
        return selectedTabIndex;
    }

    public void setSelectedGreenhouseId(Integer id)
    {
        selectedGreenhouseId.setValue(id);
    }

    public MutableLiveData<Integer> getSelectedGreenhouseId()
    {
        return this.selectedGreenhouseId;
    }

    public MutableLiveData<ArrayList<Temperature>> getTemperaturesInDateRange(String dateTimeFrom, String dateTimeTo)
    {
        temperaturesInDateRange.clear();
        temperaturesInDateRange = repo.getTemperaturesInDateRange(dateTimeFrom,dateTimeTo);

        temperaturesInRangeMld.setValue(temperaturesInDateRange);

        return this.temperaturesInRangeMld;
    }

    public ArrayList<Humidity> getHumidityInDateRange(String dateTimeFrom, String dateTimeTo)
    {
        humidityInDateRange.clear();
        humidityInDateRange = repo.getHumidityInDateRange(dateTimeFrom,dateTimeTo);

        return this.humidityInDateRange;
    }

    public ArrayList<Co2> getCo2InDateRange(String dateTimeFrom, String dateTimeTo)
    {
        co2InDateRange.clear();
        co2InDateRange = repo.getCo2InDateRange(dateTimeFrom,dateTimeTo);

        return this.co2InDateRange;
    }
}
