package org.phantom.api.event.impl.client

import org.phantom.api.event.Event

abstract class TickEvent : Event(false) {

  class Start : TickEvent()
  class End : TickEvent()

}
