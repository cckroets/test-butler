/**
 * Copyright (C) 2016 LinkedIn Corp.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.linkedin.android.testbutler.demo;

import android.content.Context;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;

import androidx.test.core.app.ApplicationProvider;

import com.linkedin.android.testbutler.TestButler;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeFalse;
import static org.junit.Assume.assumeTrue;

public class LocationServicesChangerTest {

    private LocationManager manager;

    @Before
    public void setup() {
        Context context = ApplicationProvider.getApplicationContext();
        manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }

    @Test
    public void alwaysLeavePassiveProviderEnabled() {
        assumeTrue("Only works before Android Q", Build.VERSION.SDK_INT < Build.VERSION_CODES.Q);

        // the passive provider is always enabled, regardless of the user's location preferences
        TestButler.setLocationMode(Settings.Secure.LOCATION_MODE_OFF);
        assertTrue(manager.isProviderEnabled(LocationManager.PASSIVE_PROVIDER));

        TestButler.setLocationMode(Settings.Secure.LOCATION_MODE_SENSORS_ONLY);
        assertTrue(manager.isProviderEnabled(LocationManager.PASSIVE_PROVIDER));

        TestButler.setLocationMode(Settings.Secure.LOCATION_MODE_BATTERY_SAVING);
        assertTrue(manager.isProviderEnabled(LocationManager.PASSIVE_PROVIDER));

        TestButler.setLocationMode(Settings.Secure.LOCATION_MODE_HIGH_ACCURACY);
        assertTrue(manager.isProviderEnabled(LocationManager.PASSIVE_PROVIDER));
    }

    @Test
    public void neverEnableNetworkProviderOnEmulator() {
        assumeTrue("Only works before Android Q", Build.VERSION.SDK_INT < Build.VERSION_CODES.Q);
        assumeTrue("Device is not an emulator", Build.FINGERPRINT.contains("generic"));

        // the network provider doesn't work on emulators, so nothing we do should be able to turn it on
        // http://stackoverflow.com/questions/2424564/activating-network-location-provider-in-the-android-emulator
        TestButler.setLocationMode(Settings.Secure.LOCATION_MODE_OFF);
        assertFalse(manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER));

        TestButler.setLocationMode(Settings.Secure.LOCATION_MODE_SENSORS_ONLY);
        assertFalse(manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER));

        TestButler.setLocationMode(Settings.Secure.LOCATION_MODE_BATTERY_SAVING);
        assertFalse(manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER));

        TestButler.setLocationMode(Settings.Secure.LOCATION_MODE_HIGH_ACCURACY);
        assertFalse(manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER));
    }

    @Test
    public void enableNetworkProviderOnPhysicalDevice() {
        assumeTrue("Only works before Android Q", Build.VERSION.SDK_INT < Build.VERSION_CODES.Q);
        assumeFalse("Device is an emulator", Build.FINGERPRINT.contains("generic"));

        TestButler.setLocationMode(Settings.Secure.LOCATION_MODE_OFF);
        assertFalse(manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER));

        TestButler.setLocationMode(Settings.Secure.LOCATION_MODE_SENSORS_ONLY);
        assertFalse(manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER));

        TestButler.setLocationMode(Settings.Secure.LOCATION_MODE_BATTERY_SAVING);
        assertTrue(manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER));

        TestButler.setLocationMode(Settings.Secure.LOCATION_MODE_HIGH_ACCURACY);
        assertTrue(manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER));
    }

    @Test
    public void enableGpsProviderForSensorsAndHighAccuracyOnly() {
        assumeTrue("Only works before Android Q", Build.VERSION.SDK_INT < Build.VERSION_CODES.Q);

        TestButler.setLocationMode(Settings.Secure.LOCATION_MODE_OFF);
        assertFalse(manager.isProviderEnabled(LocationManager.GPS_PROVIDER));

        TestButler.setLocationMode(Settings.Secure.LOCATION_MODE_SENSORS_ONLY);
        assertTrue(manager.isProviderEnabled(LocationManager.GPS_PROVIDER));

        TestButler.setLocationMode(Settings.Secure.LOCATION_MODE_BATTERY_SAVING);
        assertFalse(manager.isProviderEnabled(LocationManager.GPS_PROVIDER));

        TestButler.setLocationMode(Settings.Secure.LOCATION_MODE_HIGH_ACCURACY);
        assertTrue(manager.isProviderEnabled(LocationManager.GPS_PROVIDER));
    }

    @Test
    public void enableLocationModePostAndroidQ() {
        assumeTrue("Only for Android Q or later", Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q);

        TestButler.setLocationMode(Settings.Secure.LOCATION_MODE_OFF);
        assertFalse(manager.isLocationEnabled());

        TestButler.setLocationMode(Settings.Secure.LOCATION_MODE_SENSORS_ONLY);
        assertTrue(manager.isLocationEnabled());

        TestButler.setLocationMode(Settings.Secure.LOCATION_MODE_BATTERY_SAVING);
        assertTrue(manager.isLocationEnabled());

        TestButler.setLocationMode(Settings.Secure.LOCATION_MODE_HIGH_ACCURACY);
        assertTrue(manager.isLocationEnabled());
    }
}
