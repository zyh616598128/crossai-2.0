# CrossAI Flexible Input Version / CrossAI 灵活输入版本

## English Documentation

CrossAI is a flexible AI-powered e-commerce assistant that supports multiple input modalities (keywords, images, or hybrid) for automated product listing creation across multiple marketplaces.

### Key Features
- **Multi-modal Input Processing**: Text-first, Image-first, and Hybrid intelligence modes
- **Intelligent Fusion Engine**: 4 fusion strategies (reinforcement, complementation, resolution, priority)
- **Unified Content Generation**: Support for Amazon, eBay, Shopify, Walmart, Etsy
- **A/B Testing Framework**: Automated variant testing and winner promotion
- **Platform Optimization**: Tailored content adaptation for each marketplace

### Technical Architecture
- **Backend**: Spring Boot 3.2.0 with Java 17
- **Database**: MySQL 8.0 + Redis 7.x (Tencent Cloud TDSQL-C + Redis)
- **Cloud**: Tencent Cloud (COS, CDN, CLS, CVM)
- **AI Integration**: OpenAI GPT-4, DALL-E 3, Stable Diffusion
- **API**: 30+ RESTful endpoints

### Project Structure
```
backend/
├── src/main/java/com/crossai/
│   ├── controller/     # REST API controllers
│   ├── service/        # Business logic services
│   ├── model/          # JPA entity classes
│   ├── repository/     # Data access interfaces
│   ├── dto/           # Data transfer objects
│   └── config/         # Configuration classes
├── src/main/resources/ # Application configuration
└── pom.xml            # Maven dependencies
```

## 中文文档

CrossAI 是一个灵活的AI驱动的电商助手，支持多种输入模式（关键词、图像或混合），为多个市场自动创建产品listing。

### 核心功能
- **多模态输入处理**: 文本优先、图像优先、混合智能模式
- **智能融合引擎**: 4种融合策略（强化、互补、解决冲突、优先级）
- **统一内容生成**: 支持Amazon、eBay、Shopify、Walmart、Etsy
- **A/B测试框架**: 自动化变体测试和优胜者推广
- **平台优化**: 为每个市场量身定制内容适配

### 技术架构
- **后端**: Spring Boot 3.2.0 + Java 17
- **数据库**: MySQL 8.0 + Redis 7.x (腾讯云TDSQL-C + Redis)
- **云服务**: 腾讯云 (COS、CDN、CLS、CVM)
- **AI集成**: OpenAI GPT-4、DALL-E 3、Stable Diffusion
- **API**: 30+ RESTful端点

### 项目结构
```
backend/
├── src/main/java/com/crossai/
│   ├── controller/     # REST API控制器
│   ├── service/        # 业务逻辑服务
│   ├── model/          # JPA实体类
│   ├── repository/     # 数据访问接口
│   ├── dto/           # 数据传输对象
│   └── config/         # 配置类
├── src/main/resources/ # 应用配置
└── pom.xml            # Maven依赖
```

### 快速开始 / Quick Start

1. **Clone Repository** / 克隆仓库
   ```bash
   git clone git@github.com:zyh616598128/crossai-2.0.git
   cd crossai-2.0
   ```

2. **Configure Environment** / 配置环境
   ```bash
   cp infrastructure/docker/.env.example .env
   # Edit .env with your API keys
   ```

3. **Run Application** / 运行应用
   ```bash
   cd backend
   mvn spring-boot:run
   ```

### API Documentation / API文档

- **Input Processing**: `/api/input-processing/*`
- **Text Processing**: `/api/text-processing/*`
- **Image Processing**: `/api/image-processing/*`
- **Fusion Engine**: `/api/fusion/*`
- **Content Generation**: `/api/unified-content/*`

### Contributing / 贡献

Please read our contributing guidelines and submit pull requests.
请阅读贡献指南并提交Pull Request。

### License / 许可证

MIT License

---

**Development Status**: Phase 3D in progress (35% complete)
**开发状态**: 第3D阶段进行中 (完成35%)