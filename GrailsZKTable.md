# Introduction #

This tutorial gives a short introduction how to create a simple table filled with real data.

Mainly it's a wrap up of this tutorial: http://code.google.com/p/zkgrails/wiki/LoadOnDemand

You can find the source code of this tutorial [here](http://code.google.com/p/grails-zk-example/source/browse/tags/grails_zk_table_1_0/)

![http://grails-zk-example.googlecode.com/svn/wiki/images/grails-zk-table.png](http://grails-zk-example.googlecode.com/svn/wiki/images/grails-zk-table.png)

# Step by Step tutorial #

## Step 1: Install grails ##
  * Download and install the latest grails package: http://grails.org/Download

## Step 2: Create a new project ##

Open up a terminal and create a new project by typing
```
grails create-app grails-zk-table
```

## Step 3: Install ZK Plugin ##
Enter the new directory `grails-zk-table` and execute
```
grails install-plugin zk
```

## Step 4: Create a simple domain class ##
Execute `grails create-domain-class org.psm.Employee` in order to create a domain class 'Employee.groovy'

Open the file `grails-app/org/psm/Employee.groovy` and add the followin content:

```
package org.psm

class Employee {

  String username
  String fullname

  static constraints = {
  }
}
```

## Step 5: Create example values during startup ##

Open the `BootStrap.groovy` and add the following content:

```
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
```

## Step 6: Create a controller and an GSP Page ##

Execute `grails create-controller org.psm.Employee` to create a new grails controller

Add the following code to the grails controller
```
package org.psm

class EmployeeController {

  def index = {
    redirect(action: "list")
  }

  def list = { }
}
```

Now create an empty GSP page `grails-app/views/employee/list.gsp`



## Step 7: Create the basic ZUL page + Composer for the table ##

Execute `grails create-zul org.psm.employee.list`

This will create a file `list.zul` in the directory `web-app/org/psm/employee/` and a composer `ListComposer.groovy` in the directory `grails-app/org/psm/employee/`

## Step 8: Change the ZUL page ##

Open the file `list.zul` and add the following content:

```
<?xml version="1.0" encoding="UTF-8"?>
<?init class="org.zkoss.zkplus.databind.AnnotateDataBinderInit" ?>
<?variable-resolver class="org.zkoss.zkplus.spring.DelegatingVariableResolver"?>
<?page zscriptLanguage="GroovyGrails"?>

<zk xmlns="http://www.zkoss.org/2005/zul"
    xmlns:h="http://www.w3.org/1999/xhtml"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://www.zkoss.org/2005/zul http://www.zkoss.org/2005/zul/zul.xsd">

    <window apply="org.psm.employee.ListComposer" border="normal"  width="700px"  minheight="350">
        <caption label="All employees"/>

        <hbox>
            <image src="${z.resource(dir:'images', file:'grails_logo.png')}"/>
        </hbox>

        <listbox id="lstEmployee" width="100%" checkmark="true">
            <listhead sizable="true">
                <listheader label="ID" sort="auto"/>
                <listheader label="Full Name" sort="auto"/>
                <listheader label="User Name" sort="auto"/>
            </listhead>
        </listbox>
        <paging id="pagEmployee" pageSize="30"/>

    </window>

</zk>
```

> The window attribute `apply` must match the full qualified class name of the composer

The page uses the following ZK components:
  * [Window](http://books.zkoss.org/wiki/ZK_Component_Reference/Containers/Window): Renders a simple window containing the table and the paging component
  * [Listbox](http://books.zkoss.org/wiki/ZK_Component_Reference/Data/Listbox): A table, which will contain a row for every employee with 3 cells in each row for the attributes "id", "full name" and "username" of an employee
  * [Listheader](http://books.zkoss.org/wiki/ZK_Component_Reference/Data/Listbox/Listheader): 3 Header elements for each row of the table
  * [Paging](http://books.zkoss.org/wiki/ZK_Component_Reference/Supplementary/Paging): Element holding paging informations. The data required to init this component is generated in the Composer

The data rows of the table are added by the Composer in the next step


## Step 9: Change the Composer ##

Now open the file `ListComposer.groovy` and add the following content:

```
package org.psm.employee

import org.zkoss.zkgrails.*
import org.zkoss.zk.ui.event.SelectEvent
import org.psm.Employee
import org.zkoss.zk.ui.event.ForwardEvent

class ListComposer extends GrailsComposer {

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

```

The two attributes `lstEmployee` and `pagEmployee` represent the ZUL components with the same IDs, in this case the listbox component and the paging component.

So everytime you want to have direct access to a ZUL component, just give the component a unique ID and add an attribute with the exact same name as the ID of the component in the ZUL page.

If you want to react on an event, just add a method with the pattern

```
def <actionName>_<elementId>() {
Your code here
}
```

If the Listenermethod has a parameter like for example "SelectEvent", just add the parameter to the method.

## Step 10: Link the GSP page with the ZUL page ##
Add the following content to the `list.gsp` page
```
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <meta name="layout" content="main" />
        <z:head zul="/org/psm/employee/list.zul"/>
        <title>Employees</title>
    </head>
    <body>
        <div class="nav">
            <span class="menuButton"><a class="home" href="${resource(dir:'')}">Home</a></span>
        </div>
        <div class="body">
            <g:if test="${flash.message}">
            <div class="message">${flash.message}</div>
            </g:if>
            <z:body zul="/org/psm/employee/list.zul"/>
        </div>
    </body>
</html>
```

The `<z:head/>` tag is required to load all CSS and javascript files related to the list.zul page

The `<z:body/>` tag is required to render the real content of the zul page

There is the possibility to use the same naming resolving strategy for the zul pages as you expect grails to resolve GSP pages. Here's a [blog article](http://felipecypriano.com/blog/2009/12/10/grails-with-zk-embedding-zul-in-gsp/) about this feature



## Step 11: Start the app ##
Now everything should be setup to start the application.

Execute `grails run-app` to start up the application.

Now open the url http://localhost:8080/grails-zk-table/employee

If everything works as expected, you should now see a table of all employees with paging.
If you click on a row, a popup should come up with the information, what row you have selected