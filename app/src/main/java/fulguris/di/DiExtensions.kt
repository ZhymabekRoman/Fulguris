@file:JvmName("Injector")

package fulguris.di

import fulguris.App
import fulguris.settings.preferences.ConfigurationPreferences
import android.content.Context
import android.content.res.Configuration
import fulguris.device.ScreenSize
import fulguris.extensions.configId
import fulguris.settings.Config
import fulguris.settings.preferences.ConfigurationCustomPreferences
import timber.log.Timber
import java.io.File

/**
 * Provides access to current configuration settings, typically either portrait or landscape variant.
 */
val Context.configPrefs: ConfigurationPreferences
    get() {
        return configPrefs(resources.configuration)
    }

/**
 * Provides access to the configuration settings matching the given [aConfig].
 * Use this if you want to access settings for the incoming configuration from [onConfigurationChanged].
 */
fun Context.configPrefs(aConfig: Configuration): ConfigurationPreferences
    {
        return (applicationContext as App).configPreferences!!
    }

/**
 *
 */
fun Context.updateConfigPrefs() {
    val currentConfigId = this.configId
    Timber.d("updateConfigPrefs - $currentConfigId")

    // Build our list of configurations
    val directory = File(applicationInfo.dataDir, "shared_prefs")
    if (directory.exists() && directory.isDirectory) {
        val list = directory.list { _, name -> name.startsWith(Config.filePrefix) }
        // Sorting is not needed anymore as we let the PreferenceGroup do it for us now
        //list?.sortWith ( compareBy(String.CASE_INSENSITIVE_ORDER){it})

        list?.forEach {
            // Workout our domain name from the file name, skip [Config] prefix and drop .xml suffix
            val config = Config(it)

            // Check if we found the current config, used later to manage the visibility of the add config button
            if (config.id == currentConfigId) {
                (applicationContext as App).apply {
                    configPreferences = ConfigurationCustomPreferences(getSharedPreferences(config.fileName,0), ScreenSize(this@updateConfigPrefs))
                }
                Timber.d("updateConfigPrefs - Found specific config")
                // We found our config, we are done here
                return
            }
        }
    }

    // Specific config was not found, use one the generic ones
    if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
        (applicationContext as App).configPreferences = (applicationContext as App).portraitPreferences
    }
    else {
        (applicationContext as App).configPreferences = (applicationContext as App).landscapePreferences
    }

}