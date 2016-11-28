/**
 * @author Hang Su <hangsu@gatech.edu>.
 */

package edu.gatech.cse8803.model

import java.util.Date

case class Diagnostic(patientID:String, date: Date, code: String)

case class LabResult(patientID: String, itemID: String, date: Date, value: Double)

case class Medication(patientID: String, date: Date, medicine: String)

case class Patient(patientID: String, label: String)

case class LabName(testID: String, testName: String)

case class ItemName(itemID: String, itemName: String, linksTo: String)

case class Event(patientID: String, itemID: String, date: Date, value: Double, uom: String, hosp: String)

case class Feature(itemID: String, label: String, source: String)

case class Diag(patientID: String, code: String)

case class Input(patientID: String, beg: Date, t: Date, itemID: String, amount: Double)

case class Admit(patientID: String, hosp: String, event: String, time: Date)

case class ChartEvent(patientID: String, itemID: String, date: Date, value: Double)