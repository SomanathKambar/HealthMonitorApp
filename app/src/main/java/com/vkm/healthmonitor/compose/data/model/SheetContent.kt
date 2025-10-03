package com.vkm.healthmonitor.compose.data.model

data class SheetContent(
  val title: String,
  val issues: String,
  val recommendation: String,
  val severity: SliceType // or some enum / flag, so you can style accordingly
)
