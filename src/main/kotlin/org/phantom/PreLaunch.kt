package org.phantom

import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint
import org.phantom.internal.loader.AddonLoader

class PreLaunch : PreLaunchEntrypoint {

  override fun onPreLaunch() {
    AddonLoader.findAddons()
  }

}
