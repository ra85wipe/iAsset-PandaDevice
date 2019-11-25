﻿using Newtonsoft.Json;
using Newtonsoft.Json.Linq;
using System;

namespace oneM2MClient.Utils
{
    public class CustomTypeSerializer : JsonConverter
    {
        [ThreadStatic]
        static bool disabled;

        // Disables the converter in a thread-safe manner.
        bool Disabled { get { return disabled; } set { disabled = value; } }

        public override bool CanWrite { get { return !Disabled; } }

        public override bool CanConvert(Type objectType)
        {
            return true;
        }


        public override bool CanRead => false;

        public override object ReadJson(JsonReader reader, Type objectType, object existingValue, JsonSerializer serializer)
        {
            throw new NotImplementedException();
        }

        public override void WriteJson(JsonWriter writer, object value, JsonSerializer serializer)
        {
            JToken t;
            using (new PushValue<bool>(true, () => Disabled, (canWrite) => Disabled = canWrite))
            {
                t = JToken.FromObject(value, serializer);
            }


            if (t.Type != JTokenType.Object)
            {
                t.WriteTo(writer);
            }
            else
            {
                JObject o = (JObject)t;
                Type type = value.GetType();
                JObject typeWrapper = new JObject(new JProperty(type.FullName, o));

                typeWrapper.WriteTo(writer);
            }
        }
    }

    public struct PushValue<T> : IDisposable
    {
        Func<T> getValue;
        Action<T> setValue;
        T oldValue;

        public PushValue(T value, Func<T> getValue, Action<T> setValue)
        {
            if (getValue == null || setValue == null)
                throw new ArgumentNullException();
            this.getValue = getValue;
            this.setValue = setValue;
            this.oldValue = getValue();
            setValue(value);
        }

        #region IDisposable Members

        // By using a disposable struct we avoid the overhead of allocating and freeing an instance of a finalizable class.
        public void Dispose()
        {
            if (setValue != null)
                setValue(oldValue);
        }

        #endregion
    }
}
