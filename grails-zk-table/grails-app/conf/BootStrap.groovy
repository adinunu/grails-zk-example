import org.psm.Employee

class BootStrap {

  def init = { servletContext ->
    500.times { i ->
      new Employee(fullname: "Name $i", username: "user$i").save()
    }
  }
  def destroy = {
  }
}
