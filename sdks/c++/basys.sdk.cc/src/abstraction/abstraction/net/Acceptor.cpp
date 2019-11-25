#include "Acceptor.h"

#include <abstraction/impl/acceptor_impl.h>
#include <abstraction/net/Socket.h>

#include <util/util.h>

#include <log/log.h>

#include <string>


namespace basyx {
	namespace net {
		namespace tcp {

			Acceptor::Acceptor(int port) : Acceptor{ std::to_string(port) } {};
			Acceptor::Acceptor(const std::string & port)
				: acceptor{ util::make_unique<basyx::net::impl::acceptor_impl>() }
				, log{ "Acceptor" }
			{
				//ToDo: Error handling
				acceptor->listen(port);
				log.trace("Listening on port {}", port.c_str());
			}

			Acceptor::~Acceptor()
			{
			}

			Acceptor & Acceptor::operator=(Acceptor && other) { return _move_acceptor(std::move(other)); };
			Acceptor::Acceptor(Acceptor && other) { _move_acceptor(std::move(other)); };

			Acceptor & Acceptor::_move_acceptor(Acceptor && other)
			{
				// close current acceptor socket and assign new one
				if (this->acceptor != nullptr)
					this->acceptor->close();

				this->acceptor = std::move(other.acceptor);
				// other no longer represents an acceptor
				other.acceptor.reset(nullptr);
				return *this;
			}

			Socket Acceptor::accept()
			{
				//ToDo: Error handling
				return Socket{ acceptor->accept() };
			}

			void Acceptor::close()
			{
				log.trace("Closing...");
				this->acceptor->shutdown(SHUTDOWN_RDWR);
				this->acceptor->close();
			}
		}
	}
}
