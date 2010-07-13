package org.psm

import org.zkoss.zkgrails.*
import org.zkoss.zk.ui.event.ForwardEvent
import org.zkoss.zk.ui.event.SelectEvent

class EmployeeComposer extends GrailsComposer {

  def lstEmployee
  def pagEmployee

  def afterCompose = { c ->
    pagEmployee.totalSize = Employee.count()
    redraw()
  }

  def onPaging_pagEmployee(ForwardEvent fe) {
    def e = fe.origin
    redraw(e.activePage)
  }

  def redraw(page = 0) {
    def list = Employee.list(offset: page * pagEmployee.pageSize,
            max: pagEmployee.pageSize)

    lstEmployee.clear()
    lstEmployee.append {
      list.each { e ->
        listitem(value: e) {
          listcell(label: e.id)
          listcell(label: e.fullname)
          listcell(label: e.username)
        }
      }
    }
  }

  def onSelect_lstEmployee(SelectEvent se) {
    alert("Selection detected! Selected items: ${se.selectedItems*.value}")
  }

}
