package com.example.app_v1.fragments;

import android.annotation.SuppressLint;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.ToggleButton;
import com.example.app_v1.R;

import com.example.app_v1.activities.MeasurementHistoryActivity;
import com.example.app_v1.adapters.Co2RVAdapter;
import com.example.app_v1.adapters.HumidityRVAdapter;
import com.example.app_v1.adapters.TemperatureRVAdapter;
import com.example.app_v1.models.Co2;
import com.example.app_v1.models.Humidity;
import com.example.app_v1.models.Measurement;
import com.example.app_v1.models.Temperature;
import com.example.app_v1.models.Threshold;
import com.example.app_v1.viewmodels.DashboardActivityViewModel;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;
import java.util.List;

public class MeasurementDetailsFragment extends Fragment
{
    private static final String TAG = "MeasurementDetailsFragment";

    private int selectedGreenhouseId;

    private TextView titleMeasurementOverview;
    private TextView valueLatestMeasurement;
    private TextView valueMeasurementTime;
    private TextView valueMeasurementDate;
    private TextView symbolMeasurementValue;

    private TextView temperatureMinValue;
    private TextView temperatureMaxValue;

    private TextView thresholdsUnitMinValueText;
    private TextView thresholdsUnitMaxValueText;
    private TextView thresholdMinText;
    private TextView thresholdMaxText;

    private ConstraintLayout measurementOverviewDisplay;
    private ConstraintLayout recentMeasurementDisplay;
    private ConstraintLayout thresholdSettingsDisplay;

    private ConstraintLayout measurementOverviewDisplayContents;
    private ConstraintLayout recentMeasurementDisplayContent;
    private ConstraintLayout thresholdDisplayContent;

    private ConstraintLayout measurementOverviewLoadingScreen;
    private ConstraintLayout latestMeasurementsLoadingScreen;
    private ConstraintLayout thresholdsLoadingScreen;

    private ConstraintLayout noDataDisplay;
    private ConstraintLayout noDataDisplay2;
    private ConstraintLayout noDataDisplay3;

    private ProgressBar progressBarMeasurementOverview;
    private ProgressBar progressBarLatestValues;
    private ProgressBar progressBarThresholds;

    private ImageButton btnOpenHistoryActivity;
    private ImageButton btnOpenThresholdsSettings;

    private ToggleButton toggleBtnMeasurementOverviewDisplay;
    private ToggleButton toggleBtnRecentMeasurementDisplay;
    private ToggleButton toggleBtnThresholdsDisplay;
    private RadioButton radioBtnShowRecentList;
    private RadioButton radioBtnShowRecentGraph;

    private GraphView graphView;
    private LineGraphSeries<DataPoint> series;

    private RecyclerView latestMeasurementRecyclerView;
    private TemperatureRVAdapter temperatureRVAdapter;
    private HumidityRVAdapter humidityRVAdapter;
    private Co2RVAdapter co2RVAdapter;

    private ThresholdDialog thresholdDialog;

    private ArrayList<Temperature> latestTemperatures = new ArrayList<>();
    private ArrayList<Humidity> latestHumidity = new ArrayList<>();
    private ArrayList<Co2> latestCo2 = new ArrayList<>();

    private DashboardActivityViewModel dashboardActivityViewModel;

    private int tab;

    @SuppressLint("ClickableViewAccessibility")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_measurement_details,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        titleMeasurementOverview = view.findViewById(R.id.titleMeasurementOverview);
        valueLatestMeasurement = view.findViewById(R.id.valueLatestMeasurement);
        valueMeasurementTime = view.findViewById(R.id.valueMeasurementTime);
        valueMeasurementDate = view.findViewById(R.id.valueMeasurementDate);

        temperatureMinValue = view.findViewById(R.id.thresholdMin);
        temperatureMaxValue = view.findViewById(R.id.thresholdMax);

        symbolMeasurementValue = view.findViewById(R.id.symbolMeasurementValue);
        measurementOverviewDisplay = view.findViewById(R.id.measurementOverviewDisplay);
        recentMeasurementDisplay = view.findViewById(R.id.recentMeasurementDisplay);
        thresholdSettingsDisplay = view.findViewById(R.id.thresholdSettingsDisplay);
        btnOpenHistoryActivity = view.findViewById(R.id.btnOpenHistoryActivity);
        btnOpenThresholdsSettings = view.findViewById(R.id.btnOpenThresholdsSettings);
        toggleBtnMeasurementOverviewDisplay = view.findViewById(R.id.toggleBtnMeasurementOverviewDisplay);
        toggleBtnRecentMeasurementDisplay = view.findViewById(R.id.toggleBtnRecentMeasurementDisplay);
        toggleBtnThresholdsDisplay = view.findViewById(R.id.toggleBtnThresholdsDisplay);
        radioBtnShowRecentList = view.findViewById(R.id.radioBtnShowRecentList);
        radioBtnShowRecentGraph = view.findViewById(R.id.radioBtnShowRecentGraph);
        latestMeasurementRecyclerView = view.findViewById(R.id.recentMeasurementRView);
        graphView = view.findViewById(R.id.graphView);

        measurementOverviewDisplayContents = view.findViewById(R.id.measurementOverviewDisplayContents);
        recentMeasurementDisplayContent = view.findViewById(R.id.recentMeasurementDisplayContent);
        thresholdDisplayContent = view.findViewById(R.id.thresholdDisplayContent);
        measurementOverviewLoadingScreen = view.findViewById(R.id.measurementOverviewLoadingScreen);
        latestMeasurementsLoadingScreen = view.findViewById(R.id.latestMeasurementsLoadingScreen);
        thresholdsLoadingScreen = view.findViewById(R.id.thresholdsLoadingScreen);
        noDataDisplay = view.findViewById(R.id.noDataDisplay);
        noDataDisplay2 = view.findViewById(R.id.noDataDisplay2);
        noDataDisplay3 = view.findViewById(R.id.noDataDisplay3);
        progressBarMeasurementOverview = view.findViewById(R.id.progressBarMeasurementOverview);
        progressBarLatestValues = view.findViewById(R.id.progressBarLatestValues);
        progressBarThresholds = view.findViewById(R.id.progressBarThresholds);

        thresholdsUnitMinValueText = view.findViewById(R.id.textView6);
        thresholdsUnitMaxValueText = view.findViewById(R.id.textView8);
        thresholdMinText =view.findViewById(R.id.thresholdMin);
        thresholdMaxText =view.findViewById(R.id.thresholdMax);

        toggleBtnMeasurementOverviewDisplay.setBackgroundResource(R.drawable.icon_arrow_up);
        toggleBtnRecentMeasurementDisplay.setBackgroundResource(R.drawable.icon_arrow_up);
        toggleBtnThresholdsDisplay.setBackgroundResource(R.drawable.icon_arrow_up);

        radioBtnShowRecentList.setChecked(true);
        radioBtnShowRecentList.jumpDrawablesToCurrentState(); //bug fix for displaying partially checked radio button on last fragment

        graphView.setVisibility(View.GONE);
        latestMeasurementRecyclerView.setVisibility(View.VISIBLE);

        View.OnClickListener radioBtnShowRecentList_listener = new View.OnClickListener(){
            public void onClick(View v)
            {
                radioBtnShowRecentGraph.setChecked(false);
                graphView.setVisibility(View.GONE);
                latestMeasurementRecyclerView.setVisibility(View.VISIBLE);
            }
        };

        View.OnClickListener radioBtnShowRecentGraph_listener = new View.OnClickListener(){
            public void onClick(View v)
            {
                radioBtnShowRecentList.setChecked(false);
                graphView.setVisibility(View.VISIBLE);
                latestMeasurementRecyclerView.setVisibility(View.GONE);
            }
        };

        radioBtnShowRecentList.setOnClickListener(radioBtnShowRecentList_listener);
        radioBtnShowRecentGraph.setOnClickListener(radioBtnShowRecentGraph_listener);

        btnOpenThresholdsSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                                thresholdDialog = new ThresholdDialog(temperatureMinValue, temperatureMaxValue,tab);
                                thresholdDialog.show(getFragmentManager(), "ThresholdDialogFragment");

            }
        });

        graphView.removeAllSeries();

        hideLayoutContent();
        showLoadingScreens();

        dashboardActivityViewModel = ViewModelProviders.of(this.getActivity()).get(DashboardActivityViewModel.class);

        dashboardActivityViewModel.getSelectedGreenhouseId().observe(this, new Observer<Integer>()
        {
            @Override
            public void onChanged(Integer integer)
            {
                selectedGreenhouseId = integer;
            }
        });

        dashboardActivityViewModel.getSelectedTabIndex().observe(this, new Observer<Integer>()
        {
            @Override
            public void onChanged(@Nullable Integer integer)
            {
                switch(integer)
                {
                    case 0:

                        initLatestTemperatureRecyclerView();
                        graphView.removeAllSeries();

                        thresholdsUnitMinValueText.setText(getResources().getString(R.string.symbol_temperature));
                        thresholdsUnitMaxValueText.setText(getResources().getString(R.string.symbol_temperature));
                        tab=0;

                        dashboardActivityViewModel.getAllThresholds().observe(getViewLifecycleOwner(), new Observer<List<Threshold>>() {
                            @Override
                            public void onChanged(List<Threshold> thresholds) {
                                thresholdMinText.setText(thresholds.get(0).getMinValue());
                                thresholdMaxText.setText(thresholds.get(0).getMaxValue());


                            }
                        });

                        dashboardActivityViewModel.getLatestMeasurementsFromRepo().observe(getActivity(), new Observer<ArrayList<Measurement>>()
                        {
                            @Override
                            public void onChanged(final ArrayList<Measurement> measurements)
                            {
                                hideLoadingScreens();
                                showLayoutContentAfterLoading();

                                latestTemperatures = dashboardActivityViewModel.extractLatestTemperaturesFromMeasurements(measurements);

                                if(!measurements.isEmpty())
                                {
                                    hideNoDataDisplays();
                                    symbolMeasurementValue.setText(getResources().getString(R.string.symbol_temperature));

                                    valueLatestMeasurement.setText(latestTemperatures.get(0).getTemperature());
                                    valueMeasurementTime.setText(latestTemperatures.get(0).getTime());
                                    valueMeasurementDate.setText(latestTemperatures.get(0).getDate());

                                    initTemperatureGraphView(latestTemperatures);

                                    temperatureRVAdapter.clearItems();
                                    temperatureRVAdapter.setItems(latestTemperatures);
                                }
                                else
                                {
                                    hideLayoutContent();
                                    showNoDataScreens();
                                    temperatureRVAdapter.clearItems();
                                }
                            }
                        });

                        btnOpenHistoryActivity.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view)
                            {
                                String measurementType = "Temperature";
                                Intent intent = new Intent(getContext(), MeasurementHistoryActivity.class);
                                intent.putExtra("measurement_type",measurementType);
                                intent.putExtra("selectedGreenhouseId",selectedGreenhouseId);
                                startActivity(intent);
                                getActivity().finish();
                            }
                        });
                        break;

                    case 1:

                        initLatestHumidityRecyclerView();
                        graphView.removeAllSeries();

                        thresholdsUnitMinValueText.setText(getResources().getString(R.string.symbol_humidity));
                        thresholdsUnitMaxValueText.setText(getResources().getString(R.string.symbol_humidity));
                        tab=1;
                        dashboardActivityViewModel.getAllThresholds().observe(getViewLifecycleOwner(), new Observer<List<Threshold>>() {
                            @Override
                            public void onChanged(List<Threshold> thresholds) {
                                thresholdMinText.setText(thresholds.get(1).getMinValue());
                                thresholdMaxText.setText(thresholds.get(1).getMaxValue());


                            }
                        });

                        dashboardActivityViewModel.getLatestMeasurementsFromRepo().observe(getActivity(), new Observer<ArrayList<Measurement>>()
                        {
                            @Override
                            public void onChanged(ArrayList<Measurement> measurements)
                            {

                                latestHumidity = dashboardActivityViewModel.extractLatestHumidityFromMeasurements(measurements);

                                if(!measurements.isEmpty())
                                {
                                    symbolMeasurementValue.setText(getResources().getString(R.string.symbol_humidity));

                                    valueLatestMeasurement.setText(latestHumidity.get(0).getHumidity());
                                    valueMeasurementTime.setText(latestHumidity.get(0).getTime());
                                    valueMeasurementDate.setText(latestHumidity.get(0).getDate());

                                    initHumidityGraphView(latestHumidity);

                                    humidityRVAdapter.clearItems();
                                    humidityRVAdapter.setItems(latestHumidity);
                                }
                                else
                                {
                                    humidityRVAdapter.clearItems();
                                }
                            }
                        });

                        btnOpenHistoryActivity.setOnClickListener(new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View view)
                            {
                                String measurementType = "Humidity";
                                Intent intent = new Intent(getContext(), MeasurementHistoryActivity.class);
                                intent.putExtra("measurement_type",measurementType);
                                intent.putExtra("selectedGreenhouseId",selectedGreenhouseId);
                                startActivity(intent);
                                getActivity().finish();
                            }
                        });
                        break;

                    case 2:

                        initLatestCO2RecyclerView();
                        graphView.removeAllSeries();

                        thresholdsUnitMinValueText.setText(getResources().getString(R.string.symbol_co2));
                        thresholdsUnitMaxValueText.setText(getResources().getString(R.string.symbol_co2));
                        tab=2;
                        dashboardActivityViewModel.getAllThresholds().observe(getViewLifecycleOwner(), new Observer<List<Threshold>>() {
                            @Override
                            public void onChanged(List<Threshold> thresholds) {
                                thresholdMinText.setText(thresholds.get(2).getMinValue());
                                thresholdMaxText.setText(thresholds.get(2).getMaxValue());


                            }
                        });
                        dashboardActivityViewModel.getLatestMeasurementsFromRepo().observe(getActivity(), new Observer<ArrayList<Measurement>>()
                        {
                            @Override
                            public void onChanged(ArrayList<Measurement> measurements)
                            {

                                latestCo2 = dashboardActivityViewModel.extractLatestCo2FromMeasurements(measurements);

                                if(!measurements.isEmpty())
                                {
                                    symbolMeasurementValue.setText(getResources().getString(R.string.symbol_co2));

                                    valueLatestMeasurement.setText(latestCo2.get(0).getCo2());
                                    valueMeasurementTime.setText(latestCo2.get(0).getTime());
                                    valueMeasurementDate.setText(latestCo2.get(0).getDate());

                                    initCo2GraphView(latestCo2);

                                    co2RVAdapter.clearItems();
                                    co2RVAdapter.setItems(latestCo2);
                                }
                                else
                                {
                                    co2RVAdapter.clearItems();
                                }
                            }
                        });

                        btnOpenHistoryActivity.setOnClickListener(new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View view)
                            {
                                String measurementType = "Co2";
                                Intent intent = new Intent(getContext(), MeasurementHistoryActivity.class);
                                intent.putExtra("measurement_type",measurementType);
                                intent.putExtra("selectedGreenhouseId",selectedGreenhouseId);
                                startActivity(intent);
                                getActivity().finish();
                            }
                        });
                        break;

                    default:
                        break;
                }
            }
        });

        toggleBtnMeasurementOverviewDisplay.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if(isChecked)
                {
                    measurementOverviewDisplay.setVisibility(View.GONE);
                    toggleBtnMeasurementOverviewDisplay.setBackgroundResource(R.drawable.icon_arrow_down);
                }

                else
                {
                    measurementOverviewDisplay.setVisibility(View.VISIBLE);
                    toggleBtnMeasurementOverviewDisplay.setBackgroundResource(R.drawable.icon_arrow_up);
                }
            }
        });

        toggleBtnRecentMeasurementDisplay.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if(isChecked)
                {
                    recentMeasurementDisplay.setVisibility(View.GONE);
                    toggleBtnRecentMeasurementDisplay.setBackgroundResource(R.drawable.icon_arrow_down);
                }

                else
                {
                    recentMeasurementDisplay.setVisibility(View.VISIBLE);
                    toggleBtnRecentMeasurementDisplay.setBackgroundResource(R.drawable.icon_arrow_up);
                }
            }
        });

        toggleBtnThresholdsDisplay.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if(isChecked)
                {
                    thresholdSettingsDisplay.setVisibility(View.GONE);
                    toggleBtnThresholdsDisplay.setBackgroundResource(R.drawable.icon_arrow_down);
                }

                else
                {
                    thresholdSettingsDisplay.setVisibility(View.VISIBLE);
                    toggleBtnThresholdsDisplay.setBackgroundResource(R.drawable.icon_arrow_up);
                }
            }
        });

        //Return stored toggle button states after phone orientation has been changed
        if(savedInstanceState != null)
        {
            toggleBtnMeasurementOverviewDisplay.setChecked(savedInstanceState.getBoolean("toggleMeasurementDisplayBtn_state"));
            toggleBtnRecentMeasurementDisplay.setChecked(savedInstanceState.getBoolean("toggleMeasurementHistoryBtn_state"));
            toggleBtnThresholdsDisplay.setChecked(savedInstanceState.getBoolean("toggleThresholdSettingsBtn_state"));
            radioBtnShowRecentList.setChecked(savedInstanceState.getBoolean("radioBtnShowList_state"));
            radioBtnShowRecentGraph.setChecked(savedInstanceState.getBoolean("radioBtnShowGraph_state"));
        }
    }

    private void hideLayoutContent()
    {
        measurementOverviewDisplayContents.setVisibility(View.GONE);
        recentMeasurementDisplayContent.setVisibility(View.GONE);
        thresholdDisplayContent.setVisibility(View.GONE);
    }

    private void showLayoutContentAfterLoading()
    {
        measurementOverviewDisplayContents.setVisibility(View.VISIBLE);
        recentMeasurementDisplayContent.setVisibility(View.VISIBLE);
        thresholdDisplayContent.setVisibility(View.VISIBLE);
    }

    private void showLoadingScreens()
    {
        measurementOverviewLoadingScreen.setVisibility(View.VISIBLE);
        latestMeasurementsLoadingScreen.setVisibility(View.VISIBLE);
        thresholdsLoadingScreen.setVisibility(View.VISIBLE);
        progressBarMeasurementOverview.setVisibility(View.VISIBLE);
        progressBarLatestValues.setVisibility(View.VISIBLE);
        progressBarThresholds.setVisibility(View.VISIBLE);
    }

    private void hideLoadingScreens()
    {
        measurementOverviewLoadingScreen.setVisibility(View.GONE);
        latestMeasurementsLoadingScreen.setVisibility(View.GONE);
        thresholdsLoadingScreen.setVisibility(View.GONE);
        progressBarMeasurementOverview.setVisibility(View.GONE);
        progressBarLatestValues.setVisibility(View.GONE);
        progressBarThresholds.setVisibility(View.GONE);
    }

    private void showNoDataScreens()
    {
        noDataDisplay.setVisibility(View.VISIBLE);
        noDataDisplay2.setVisibility(View.VISIBLE);
        noDataDisplay3.setVisibility(View.VISIBLE);
    }

    private void hideNoDataDisplays()
    {
        noDataDisplay.setVisibility(View.GONE);
        noDataDisplay2.setVisibility(View.GONE);
        noDataDisplay3.setVisibility(View.GONE);
    }

    private void initLatestTemperatureRecyclerView()
    {
        temperatureRVAdapter = new TemperatureRVAdapter(getActivity());

        temperatureRVAdapter.clearItems();
        latestMeasurementRecyclerView.hasFixedSize();
        latestMeasurementRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        latestMeasurementRecyclerView.setAdapter(temperatureRVAdapter);
    }

    private void initLatestHumidityRecyclerView()
    {
        humidityRVAdapter = new HumidityRVAdapter(getActivity());

        humidityRVAdapter.clearItems();
        latestMeasurementRecyclerView.hasFixedSize();
        latestMeasurementRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        latestMeasurementRecyclerView.setAdapter(humidityRVAdapter);
    }

    private void initLatestCO2RecyclerView()
    {
        co2RVAdapter = new Co2RVAdapter(getActivity());

        co2RVAdapter.clearItems();
        latestMeasurementRecyclerView.hasFixedSize();
        latestMeasurementRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        latestMeasurementRecyclerView.setAdapter(co2RVAdapter);
    }

    private void initTemperatureGraphView(ArrayList<Temperature> temperatures)
    {
        if(temperatures != null)
        {
            graphView.removeAllSeries();

            series = new LineGraphSeries<>(new DataPoint[]{
                    new DataPoint(0, Double.valueOf(temperatures.get(4).getTemperature())),
                    new DataPoint(1, Double.valueOf(temperatures.get(3).getTemperature())),
                    new DataPoint(2, Double.valueOf(temperatures.get(2).getTemperature())),
                    new DataPoint(3, Double.valueOf(temperatures.get(1).getTemperature())),
                    new DataPoint(4, Double.valueOf(temperatures.get(0).getTemperature()))
            });

            graphView.addSeries(series);
            series.setColor(Color.RED);
            series.setDrawDataPoints(true);

            StaticLabelsFormatter staticLabelsFormatter = new StaticLabelsFormatter(graphView);
            staticLabelsFormatter.setHorizontalLabels(
                    new String[]{temperatures.get(4).getTime(),
                            temperatures.get(3).getTime(),
                            temperatures.get(2).getTime(),
                            temperatures.get(1).getTime(),
                            temperatures.get(0).getTime()});

            graphView.getViewport().setXAxisBoundsManual(true);
            graphView.getGridLabelRenderer().setHumanRounding(true);
            graphView.getGridLabelRenderer().setNumHorizontalLabels(10);
            graphView.getGridLabelRenderer().setVerticalAxisTitleTextSize(30f);
            graphView.getGridLabelRenderer().setLabelVerticalWidth(50);
            graphView.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);
            graphView.getGridLabelRenderer().reloadStyles();
        }
    }

    private void initHumidityGraphView(ArrayList<Humidity> humiditys)
    {
        if(humiditys != null)
        {
            graphView.removeAllSeries();

            series = new LineGraphSeries<>(new DataPoint[]{
                    new DataPoint(0, Double.valueOf(humiditys.get(4).getHumidity())),
                    new DataPoint(1, Double.valueOf(humiditys.get(3).getHumidity())),
                    new DataPoint(2, Double.valueOf(humiditys.get(2).getHumidity())),
                    new DataPoint(3, Double.valueOf(humiditys.get(1).getHumidity())),
                    new DataPoint(4, Double.valueOf(humiditys.get(0).getHumidity()))
            });

            graphView.addSeries(series);
            series.setColor(Color.RED);
            series.setDrawDataPoints(true);

            StaticLabelsFormatter staticLabelsFormatter = new StaticLabelsFormatter(graphView);
            staticLabelsFormatter.setHorizontalLabels(
                    new String[]{humiditys.get(4).getTime(),
                            humiditys.get(3).getTime(),
                            humiditys.get(2).getTime(),
                            humiditys.get(1).getTime(),
                            humiditys.get(0).getTime()});

            graphView.getViewport().setXAxisBoundsManual(true);
            graphView.getGridLabelRenderer().setHumanRounding(true);
            graphView.getGridLabelRenderer().setNumHorizontalLabels(10);
            graphView.getGridLabelRenderer().setVerticalAxisTitleTextSize(30f);
            graphView.getGridLabelRenderer().setLabelVerticalWidth(50);
            graphView.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);
            graphView.getGridLabelRenderer().reloadStyles();
        }
    }

    private void initCo2GraphView(ArrayList<Co2> co2s)
    {
        if(co2s != null)
        {
            graphView.removeAllSeries();

            series = new LineGraphSeries<>(new DataPoint[]{
                    new DataPoint(0, Double.valueOf(co2s.get(4).getCo2())),
                    new DataPoint(1, Double.valueOf(co2s.get(3).getCo2())),
                    new DataPoint(2, Double.valueOf(co2s.get(2).getCo2())),
                    new DataPoint(3, Double.valueOf(co2s.get(1).getCo2())),
                    new DataPoint(4, Double.valueOf(co2s.get(0).getCo2()))
            });

            graphView.addSeries(series);
            series.setColor(Color.RED);
            series.setDrawDataPoints(true);

            StaticLabelsFormatter staticLabelsFormatter = new StaticLabelsFormatter(graphView);
            staticLabelsFormatter.setHorizontalLabels(
                    new String[]{co2s.get(4).getTime(),
                            co2s.get(3).getTime(),
                            co2s.get(2).getTime(),
                            co2s.get(1).getTime(),
                            co2s.get(0).getTime()});

            graphView.getViewport().setXAxisBoundsManual(true);
            graphView.getGridLabelRenderer().setHumanRounding(true);
            graphView.getGridLabelRenderer().setNumHorizontalLabels(10);
            graphView.getGridLabelRenderer().setVerticalAxisTitleTextSize(30f);
            graphView.getGridLabelRenderer().setLabelVerticalWidth(50);
            graphView.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);
            graphView.getGridLabelRenderer().reloadStyles();
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState)
    {
        //Save toggle button states when changing phone orientation
        outState.putBoolean("toggleMeasurementDisplayBtn_state", toggleBtnMeasurementOverviewDisplay.isChecked());
        outState.putBoolean("toggleMeasurementHistoryBtn_state", toggleBtnRecentMeasurementDisplay.isChecked());
        outState.putBoolean("toggleThresholdSettingsBtn_state", toggleBtnThresholdsDisplay.isChecked());
        outState.putBoolean("radioBtnShowList_state", radioBtnShowRecentList.isChecked());
        outState.putBoolean("radioBtnShowGraph_state", radioBtnShowRecentGraph.isChecked());

        super.onSaveInstanceState(outState);
    }

    @Override
    public void onResume()
    {
        if(latestTemperatures.size() > 0 || latestHumidity.size() > 0 || latestCo2.size() > 0)
        {
            showLayoutContentAfterLoading();
        }

        else
        {
            hideNoDataDisplays();
            hideLayoutContent();
            showLoadingScreens();
        }
        //showLayoutContentAfterLoading();
        super.onResume();
    }

    @Override
    public void onDestroy()
    {
        getActivity().finish();
        super.onDestroy();
    }
}