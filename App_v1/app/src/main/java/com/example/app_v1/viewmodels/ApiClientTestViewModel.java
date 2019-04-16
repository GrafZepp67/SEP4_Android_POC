package com.example.app_v1.viewmodels;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.example.app_v1.models.Temperature;
import com.example.app_v1.repositories.ApiClientTestWithRepository;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

public class ApiClientTestViewModel extends ViewModel
{
    private LiveData<Temperature> ldTemp;
    private ApiClientTestWithRepository apiRepo;

    public void init()
    {
        if(ldTemp != null)
        {
            return;
        }

        apiRepo = ApiClientTestWithRepository.getInstance();
        ldTemp = apiRepo.getLastTemperature();
    }

    public LiveData<Temperature> getTemp()
    {
        if(ldTemp == null)
        {
            apiRepo = ApiClientTestWithRepository.getInstance();
            ldTemp = apiRepo.getLastTemperature();
        }

        return ldTemp;
    }
}
