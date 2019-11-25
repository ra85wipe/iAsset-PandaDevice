/*
 * BaSyxNativeGetFrame.h
 *
 *  Created on: 08.08.2018
 *      Author: schnicke
 */

#ifndef VAB_VAB_PROVIDER_BASYX_FRAME_BASYXNATIVEFRAMEHELPER_H
#define VAB_VAB_PROVIDER_BASYX_FRAME_BASYXNATIVEFRAMEHELPER_H


#include "basyx/types.h"

#include "util/tools/StringTools.h"

#include <iostream>
#include <sstream>
#include <string>


namespace basyx {
namespace vab {
namespace provider {
namespace native {
namespace frame {


    class BaSyxNativeFrameHelper {
    public:
        /**
	 * Retrieves a string from an array
	 * The assumed array format is:
	 * 	repeated:
	 * 		4 byte string size
	 * 		N byte string data
	 */
    static std::string getString(char const* data, std::size_t num)
    {
        for (std::size_t i = 0; i < num; i++) {
            std::size_t stringSize = CoderTools::getInt32(data, 0);

            // Increment data pointer to skip string size value and the string itself
            data += BASYX_STRINGSIZE_SIZE + stringSize;
        }

        return StringTools::fromArray(data);
    }

    static void printFrame(char const* data, size_t size)
    {
		basyx::log::topic("Frame").debug("+-----------+");
		basyx::log::topic("Frame").debug("| Size: {}", (int)data[0]);
		basyx::log::topic("Frame").debug("+-----------+");
		basyx::log::topic("Frame").debug("| Command: 0x{0:x}", data[4]);
		basyx::log::topic("Frame").debug("+-----------+");

        data += 5;
        size -= 5;

        // Iterate over the array to find all strings
		while (size > 0) {
			std::string str = StringTools::fromArray(data);
			basyx::log::topic("Frame").debug("| Len: {}", (int)data[0]);
			basyx::log::topic("Frame").debug("| Data: {}", str);
			basyx::log::topic("Frame").debug("+-----------+");
			data += str.length() + BASYX_STRINGSIZE_SIZE;
			size -= str.length() + BASYX_STRINGSIZE_SIZE;
		}
    }

    /**
* Retrieves the command from a basyx frame and writes the size of the command in commandSize
*/
    static int getCommand(char const* data, std::size_t* commandSize)
    {
        *commandSize = 1;
        return data[0];
    }
};

}
}
}
}
}

#endif /* VAB_VAB_PROVIDER_BASYX_FRAME_BASYXNATIVEFRAMEHELPER_H */
