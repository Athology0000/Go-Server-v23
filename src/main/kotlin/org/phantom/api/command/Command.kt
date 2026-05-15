package org.phantom.api.command

abstract class Command(
  val name: String,
  val aliases: Array<String> = emptyArray(),
)
