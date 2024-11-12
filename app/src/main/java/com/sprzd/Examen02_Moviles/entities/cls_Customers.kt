package com.sprzd.Examen02_Moviles.entities

class cls_Customers {
    var IDCustomer: String = ""
    var contactName: String = ""
    var contactTitle: String = ""
    var shipName: String = ""
    var shipAddress: String = ""
    var shipCity: String = ""
    var shipRegion: String = ""
    var shipPostalCode: String = ""
    var shipCountry: String = ""

    constructor() {}

    constructor(IDCustomer: String, contactName: String, contactTitle: String, shipName: String, shipAddress: String, shipCity: String, shipRegion: String, shipPostalCode: String, shipCountry: String) {
        this.IDCustomer = IDCustomer
        this.contactName = contactName
        this.contactTitle = contactTitle
        this.shipName = shipName
        this.shipAddress = shipAddress
        this.shipCity = shipCity
        this.shipRegion = shipRegion
        this.shipPostalCode = shipPostalCode
        this.shipCountry = shipCountry
    }
}
