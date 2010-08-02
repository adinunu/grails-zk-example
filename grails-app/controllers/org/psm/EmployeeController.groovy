package org.psm

class EmployeeController {

  def index = {
    redirect(action: "list")
  }

  def list = { }
}
