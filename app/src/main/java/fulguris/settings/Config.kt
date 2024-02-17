package fulguris.settings

import android.content.Context
import fulguris.R
import fulguris.app
import fulguris.settings.preferences.DomainPreferences
import timber.log.Timber
import java.io.File

/**
 * Settings configuration model
 */
class Config(idOrFile: String) {

    val id: String

    init {
        // Allow to create a config object from its id or from its file name
        // Just remove file prefix and suffix to obtain our config id
        id = idOrFile/*.replace(filePrefix, "")*/.replace(fileSuffix,"")
    }

    /**
     * User friendly localized named
     */
    fun name(aContext: Context): String {
        var name = id.replace(filePrefix,  "")
        name = name.replace("-",  " - ")
        name = name.replace(" - sw",  "° - sw")
        name = name.replace("landscape", aContext.getString(R.string.settings_title_landscape))
        name = name.replace("portrait", aContext.getString(R.string.settings_title_portrait))
        return "${name}dp"
    }

    /**
     *
     */
    val fileName: String get() = id

    /**
     *
     */
    val fullFileName: String get () = app.applicationInfo.dataDir + "/shared_prefs/" + fileName + fileSuffix

    /**
     * Delete the settings file belonging to this config.
     */
    fun delete() {
        val file = File(fullFileName)
        Timber.d("Delete ${file.absolutePath}: ${file.delete()}")
    }


    companion object {
        const val filePrefix = "[Config]"
        const val fileSuffix = ".xml"
    }

}