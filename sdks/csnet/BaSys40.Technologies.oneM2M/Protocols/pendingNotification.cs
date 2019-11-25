namespace oneM2MClient.Protocols
{
using System;
using System.Diagnostics;
using System.Xml.Serialization;
using System.Collections;
using System.Xml.Schema;
using System.ComponentModel;
using System.Xml;
using System.Collections.Generic;
public enum pendingNotification {
    
    [System.Xml.Serialization.XmlEnumAttribute("1")]
    SendLatest = 1,
    
    [System.Xml.Serialization.XmlEnumAttribute("2")]
    SendAllPending = 2,
}
}
